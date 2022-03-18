<?php
header('Access-Control-Allow-Origin: *');
header("Access-Control-Allow-Credentials: true");
header('Access-Control-Allow-Methods: GET, PUT, POST, DELETE, OPTIONS');
header('Access-Control-Max-Age: 1000');
header('Access-Control-Allow-Headers: Origin, Content-Type, X-Auth-Token , Authorization, token');
include('../util/authentication.php');
include('../util/notification_queries.php');

include('../util/database.php');
include('../util/error_handling.php');
include('../Models/notification.php');


$json = file_get_contents('php://input');
$data = json_decode($json);
if (isset($_GET['f'])) {
    if (function_exists($_GET['f'])) {
        $_GET['f']($data);
    }
}

function addNotification($data)
{
    $mysqli = getConn();
    $data = json_decode(json_encode($data));
    $user = $data->user;
    $dateNot = $data->dateNot;
    $title = $data->title;
    $body = $data->body;
    $status = $data->status;

    add_notification($mysqli, $user, $dateNot, $title, $body, $status);
    if ($mysqli->error) {
        throwError($mysqli, $mysqli->error, 503);
    }
    $mysqli->close();
}

function editNotification($data)
{
    $mysqli = getConn();
    $data = json_decode(json_encode($data));
    $status = $data->status;
    $token = getallheaders()['token'];
    $user = User::auth($mysqli, $token);

    edit_notification($mysqli, $user->email, $status);
    if ($mysqli->error) {
        throwError($mysqli, $mysqli->error, 503);
    }
    $mysqli->close();
}

function getNotification()
{
    $mysqli = getConn();
    $token = getallheaders()['token'];
    $user = User::auth($mysqli, $token);

    $notification = get_notification($mysqli, $user->email);
    if ($mysqli->error) {
        throwError($mysqli, $mysqli->error, 503);
    }
    $arr = array();
    foreach ($notification as $not) {
        if (strcasecmp('Not seen', $not['status']) == 0) {
            $arr[] = new Notification(
                $not['id'],
                $not['user'],
                $not['dateNot'],
                $not['title'],
                $not['body'],
                $not['status']
            );
        }
    }
    echo json_encode($arr);
    $mysqli->close();
}
