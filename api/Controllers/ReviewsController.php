<?php
header('Access-Control-Allow-Origin: *');
header("Access-Control-Allow-Credentials: true");
header('Access-Control-Allow-Methods: GET, PUT, POST, DELETE, OPTIONS');
header('Access-Control-Max-Age: 1000');
header('Access-Control-Allow-Headers: Origin, Content-Type, X-Auth-Token , Authorization, token');
include('../util/reviews_queries.php');
include('../util/database.php');
include('../util/authentication.php');
include('../util/user_queries.php');


$json = file_get_contents('php://input');
$data = json_decode($json);
$data = json_decode($json);

if (isset($_GET['f'])) {
    if (function_exists($_GET['f'])) {
        $_GET['f']($data);
    }
}

function getServiceReviews($data)
{
    $mysqli = getConn();
    $serviceId = $data->serviceId;
    $reviews = ServiceReview::fetch_service_reviews($mysqli, $serviceId);
    if (!empty($mysqli->error)) {
        throwError($mysqli, $mysqli->error, 503);
    }
    $response = new stdClass();
    $response->reviews = $reviews;
    echo json_encode($response);
    $mysqli->close();
}

function addServiceReview($data)
{
    $mysqli = getConn();
    $token =  $token = getallheaders()['token'];
    $user = User::auth($mysqli, $token);
    $description = $data->description;
    $rating = $data->rating;
    $date = date("Y-m-d");
    $ids = $data->ids;
    $organizer = $user->email;
    $pictures = $data->pictures;
    ServiceReview::add_service_review($mysqli, $description, $rating, $date, $ids, $organizer, $pictures);
    if (!empty($mysqli->error)) {
        throwError($mysqli, $mysqli->error, 503);
    }
}

function deleteServiceReview($data)
{
    $mysqli = getConn();
    $token =  $token = getallheaders()['token'];
    $user = User::auth($mysqli, $token);
    $idsr = $data->idsr;
    ServiceReview::delete_service_review($mysqli, $idsr, $user->email);
    if (!empty($mysqli->error)) {
        throwError($mysqli, $mysqli->error, 503);
    }
    $mysqli->close();
}

function editServiceReview($data)
{
    $mysqli = getConn();
    $token = getallheaders()['token'];
    $idsr = $data->idsr;
    $user = User::auth($mysqli, $token);
    $description = $data->description;
    $rating = $data->rating;
    $date = date("Y-m-d");
    $pictures = $data->pictures;
    ServiceReview::update_service_review($mysqli, $idsr, $description, $rating, $date, $pictures, $user->email);
    if (!empty($mysqli->error)) {
        throwError($mysqli, $mysqli->error, 503);
    }
    $mysqli->close();
}

/////////////////////////////////////////////////////
function getEventReviews($data)
{
    $mysqli = getConn();
    $eventId = $data->eventId;
    $reviews = EventReview::fetch_event_reviews($mysqli, $eventId);
    var_dump($reviews);
    if (!empty($mysqli->error)) {
        throwError($mysqli, $mysqli->error, 503);
    }
    $response = new stdClass();
    $response->reviews = $reviews;
    echo json_encode($response);
    $mysqli->close();
}

function deleteEventReview($data)
{
    $mysqli = getConn();
    $token =  $token = getallheaders()['token'];
    $user = User::auth($mysqli, $token);
    $ider = $data->ider;
    EventReview::delete_event_review($mysqli, $ider, $user->email);
    if (!empty($mysqli->error)) {
        throwError($mysqli, $mysqli->error, 503);
    }
    $mysqli->close();
}



function addEventReview($data)
{
    $mysqli = getConn();
    $token =  $token = getallheaders()['token'];
    $user = User::auth($mysqli, $token);
    $comment = $data->comment;
    $rating = $data->rating;
    $ide = $data->ide;
    $reviewer_fname = $user->first_name;
    $reviewer_lname = $user->last_name;

    $pictures = $data->pictures;
    $userEmail = $user->email;
    EventReview::add_event_review($mysqli, $comment, $rating, $reviewer_fname, $reviewer_lname, $ide, $pictures, $userEmail);
    if (!empty($mysqli->error)) {
        throwError($mysqli, $mysqli->error, 503);
    }
}

function editEventReview($data)
{
    $mysqli = getConn();
    $token = getallheaders()['token'];
    $user = User::auth($mysqli, $token);
    $comment = $data->comment;
    $rating = $data->rating;
    $ider = $data->ider;
    $pictures = $data->pictures;
    EventReview::update_event_review($mysqli, $ider, $comment, $rating, $pictures, $user->email);

    if (!empty($mysqli->error)) {
        throwError($mysqli, $mysqli->error, 503);
    }
    $mysqli->close();
}
