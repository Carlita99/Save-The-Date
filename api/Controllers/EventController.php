<?php
header('Access-Control-Allow-Origin: *');
header("Access-Control-Allow-Credentials: true");
header('Access-Control-Allow-Methods: GET, PUT, POST, DELETE, OPTIONS');
header('Access-Control-Max-Age: 1000');
header('Access-Control-Allow-Headers: Origin, Content-Type, X-Auth-Token , Authorization, token');
include('../util/database.php');
include('../util/events_queries.php');
include('../util/error_handling.php');
include('../Models/event_type.php');
include('../Models/event.php');

include('../Models/User.php');
include('../util/authentication.php');

$json = file_get_contents('php://input');
$data = json_decode($json);

if (isset($_GET['f'])) {
    if (function_exists($_GET['f'])) {
        $_GET['f']($data);
    }
}





function getEventTypes($data)
{

    $mysqli = getConn();
    $result = EventType::fetchEventTypes($mysqli);
    if (!empty($mysqli->error)) {
        throwError($mysqli, $mysqli->error, 503);
    }

    $eventTypes = new stdClass();
    $eventTypes->eventTypes = $result;
    echo json_encode($eventTypes);
    $mysqli->close();
}

function getEvents($data)
{
    $mysqli = getConn();
    $arr = Event::fetchEvents($mysqli);
    if (!empty($mysqli->error)) {
        throwError($mysqli, $mysqli->error, 503);
    }

    $events = new stdClass();
    $events->events = $arr;
    echo json_encode($events);
    $mysqli->close();
}

function getSingleEvent($data)
{
    $mysqli = getConn();
    if (isset($data->id)) {
        $result = Event::fetchSingleEvent($mysqli, $data->id);
        if (!empty($mysqli->error)) {
            throwError($mysqli, $mysqli->error, 503);
        }
        if (!empty($result)) {

            $event = new stdClass();
            $event->event = $result;
            echo json_encode($event);
            return;
        } else {

            throwError($mysqli, "Event not found.", 404);
        }
    } else {
        throwError($mysqli, "Event not found.", 404);
    }
    $mysqli->close();
}

function editEvent($data)
{
    $token = getallheaders()['token'];
    $mysqli = getConn();
    $user = User::auth($mysqli, $token);
    $id = $data->event->id;
    $name = $data->event->name;
    $numberOfGuests = $data->event->numberOfGuests;
    $today = date("Y-m-d");
    $date = $data->event->date;
    if ($today >= $date) {

        throwError($mysqli, "Date can't be before tomorrow.", 402);
    }
    $startingHour = $data->event->startingHour;
    $duration = $data->event->duration;
    $description = $data->event->description;
    $highlights = $data->event->highlights;
    $cost = $data->event->cost;
    $typeEvent = $data->event->typeEvent;
    $pictures = $data->event->pictures;
    $event = Event::fetchSingleEvent($mysqli, $id);
    $event->name = $name;
    $event->number_of_guests = $numberOfGuests;
    $event->date = $date;
    $event->starting_hour = $startingHour;
    $event->duration = $duration;
    $event->description = $description;
    $event->highlights = $highlights;
    $event->total_cost = $cost;
    $event->typeE = $typeEvent;
    $event->pictures = $pictures;
    $event->updateEvent($mysqli, $user->email);
    if (!empty($mysqli->error)) {
        throwError($mysqli, $mysqli->error, 503);
    }
    $mysqli->close();
}

function deleteEvent($data)
{
    $mysqli = getConn();
    $token = getallheaders()['token'];
    $user = User::auth($mysqli, $token);
    $id = strval($data->id);
    $event = Event::fetchSingleEvent($mysqli, $id);
    if ($event->organizer != $user->email) {
        throwError($mysqli, "Unauthorized Access.", 401);
    }
    $event->deleteEventRecord($mysqli);
    if (!empty($mysqli->error))
        throwError($mysqli, $mysqli->error, 503);

    $mysqli->close();
}

function addEvent($data)
{
    $mysqli = getConn();
    $token = getallheaders()['token'];
    $user = User::auth($mysqli, $token);
    $today = date("Y-m-d");
    $date = $data->event->date;
    if ($today >= $date) {
        throwError($mysqli, "Date can't be before tomorrow.", 503);
    }

    $startingHour = $data->event->startingHour;
    $duration = $data->event->duration;
    $description = $data->event->description;
    $highlights = $data->event->highlights;
    $nom = $data->event->nom;
    $numberOfGuests = $data->event->numberOfGuests;
    $cost = $data->event->cost;
    $organizer = $user->email;
    $typeEvent = $data->event->typeEvent;
    $pictures = $data->event->pictures;
    $eventToAdd = new Event(null, $nom, $numberOfGuests, $date, $startingHour, $duration, $description, $highlights, $cost, $organizer, $typeEvent, $pictures);
    $result = $eventToAdd->addEventRecord($mysqli, $nom, $numberOfGuests, $date, $startingHour, $duration, $description, $highlights, $cost, $organizer, $typeEvent, $pictures);
    if ($mysqli->error) {
        throwError($mysqli, $mysqli->error, 503);
    }

    $event = new stdClass();
    $event->event = $result;
    if (!empty($mysqli->error)) {
        throwError($mysqli, $mysqli->error, 503);
    }
    echo (json_encode($event));
    $mysqli->close();
}

function getOrganizersandEvents($data)
{
    $mysqli = getConn();
    $events = fetch_events_and_organizers($mysqli);
    if (!empty($mysqli->error)) {
        throwError($mysqli, $mysqli->error, 503);
    }
    $response = new stdClass();
    $response->events = $events;
    echo json_encode($response);
    $mysqli->close();
}
