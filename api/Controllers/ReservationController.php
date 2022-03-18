<?php
header('Access-Control-Allow-Origin: *');
header("Access-Control-Allow-Credentials: true");
header('Access-Control-Allow-Methods: GET, PUT, POST, DELETE, OPTIONS');
header('Access-Control-Max-Age: 1000');
header('Access-Control-Allow-Headers: Origin, Content-Type, X-Auth-Token , Authorization, token');
include('../util/authentication.php');
include('../Models/service_reservation.php');
include('../util/events_queries.php');
include('../util/service_queries.php');
include('../Models/event.php');
include('../Models/user.php');
include('../Models/Service.php');



include('../util/database.php');
include('../util/error_handling.php');


$json = file_get_contents('php://input');
$data = json_decode($json);
$data = json_decode($json);

if (isset($_GET['f'])) {
    if (function_exists($_GET['f'])) {
        $_GET['f']($data);
    }
}


function getEventReservations($data)
{
    $ide = $data->ide;
    // $token = getallheaders()['token'];
    $mysqli = getConn();
    // $user = auth($mysqli, $token);
    $event = Event::fetchSingleEvent($mysqli, $ide);
    if (empty($event)) {
        throwError($mysqli, "Event not found.", 404);
    }
    // if ($event['organizer'] != $user->email) {
    //     throwError($mysqli, "Unauthorized Access! This event isn't of your creation.", 401);
    // }
    $reservations = ServiceReservation::fetch_event_service_reservations($mysqli, $ide);
    if ($mysqli->error) {
        throwError($mysqli, $mysqli->error, 503);
    }
    $arr = array();

    echo json_encode($arr);
    $mysqli->close();
}

function deleteReservationByids($data)
{
    $ids = $data->ids;
    $ide = $data->ide;
    $mysqli = getConn();
    ServiceReservation::deleteByIds($mysqli, $ide, $ids);
    if ($mysqli->error) {
        throwError($mysqli, "Server Error, please try again", 504);
    }
    $mysqli->close();
}

function getServiceReservations($data)
{
    $mysqli = getConn();
    $user = User::auth($mysqli, (getallheaders()['token']));
    $serviceId = $data->service;
    $service = Service::fetch_single_service($mysqli, $serviceId);
    if (empty($service)) {
        throwError($mysqli, "Service Not Found", 404);
    }
    if ($service->provider != $user->email) {
        throwError($mysqli, "Unauthorized Access! This service isn't of your creation.", 401);
    }
    $reservations = ServiceReservation::fetch_service_reservations($mysqli, $serviceId);
    if ($mysqli->error) {
        throwError($mysqli, $mysqli->error, 503);
    }

    echo json_encode($reservations);
    $mysqli->close();
}
function addServiceReservation($data)
{
    $mysqli = getConn();
    $data = json_decode(json_encode($data));
    $ide = $data->ide;
    $ids = $data->ids;
    $price = $data->price;
    $token = getallheaders()['token'];
    $user = User::auth($mysqli, $token);
    $event = Event::fetchSingleEvent($mysqli, $ide);
    if (empty($event)) {
        throwError($mysqli, "Event not found.", 404);
    }

    if ($event->organizer != $user->email) {
        throwError($mysqli, "Unauthorized Access! This event isn't of your creation.", 401);
    }

    $date = $event->date;
    $reservation = new ServiceReservation(null, 0, $date, $ide, $ids, null);
    $reservation->addReservation($mysqli, $ide, $ids, $date, $price);
    if ($mysqli->error) {
        throwError($mysqli, $mysqli->error, 503);
    }
    $mysqli->close();
}
////Not satatic if possible

function editServiceReservation($data)
{
    $mysqli = getConn();
    $user = User::auth($mysqli, (getallheaders()['token']));
    $res = ServiceReservation::editReservation($mysqli, $user->email, $data->id, $data->date, $data->price, $data->confirmed);
    if ($mysqli->error) {
        throwError($mysqli, $mysqli->error, 503);
    }
    $res = new ServiceReservation($res['id'], $res['price'], $res['date'], $res['ide'], $res['ids'], $res['confirmed']);
    $response = new stdClass();
    $response->reservation = $res;
    $mysqli->close();
}

function deleteReservation($data)
{

    $mysqli = getConn();
    $user = User::auth($mysqli, (getallheaders()['token']));
    ServiceReservation::delete_reservation($mysqli, $user->email, $data->id);
}
