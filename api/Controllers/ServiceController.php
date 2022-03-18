<?php


include('../util/database.php');
include('../util/service_queries.php');
include('../util/error_handling.php');
include('../Models/service_type.php');
include('../Models/service.php');
include('../Models/user.php');

include('../util/authentication.php');
header('Access-Control-Allow-Origin: *');
header("Access-Control-Allow-Credentials: true");
header('Access-Control-Allow-Methods: GET, PUT, POST, DELETE, OPTIONS');
header('Access-Control-Max-Age: 1000');
header('Access-Control-Allow-Headers: Origin, Content-Type, X-Auth-Token , Authorization, token');

$json = file_get_contents('php://input');
$data = json_decode($json);
$data = json_decode($json);

if (isset($_GET['f'])) {
    if (function_exists($_GET['f'])) {
        $_GET['f']($data);
    }
}

function getServices($data)
{
    $mysqli = getConn();
    $arr = Service::fetch_services($mysqli);
    if (!empty($mysqli->error)) {
        throwError($mysqli, $mysqli->error, 503);
    }

    $services = new stdClass();
    $services->services = $arr;
    echo json_encode($services);
    $mysqli->close();
}

function getService($data)
{
    $mysqli = getConn();
    $id = $data->id;
    $result = Service::fetch_single_service($mysqli, $id);
    if (!empty($mysqli->error)) {
        throwError($mysqli, $mysqli->error, 503);
    }

    if (!isset($result)) {
        throwError($mysqli, "Service not found.", 404);
    }


    $service = new stdClass();
    $service->service = $result;
    echo json_encode($service);
    $mysqli->close();
}


function getProvidersandServices($data)
{
    $mysqli = getConn();
    $services = fetch_services_and_providers($mysqli);

    if (!empty($mysqli->error)) {
        throwError($mysqli, $mysqli->error, 503);
    }
    $response = new stdClass();
    $response->services = $services;
    echo json_encode($response);
    $mysqli->close();
}



function getServiceTypes($data)
{

    $mysqli = getConn();
    $result = ServiceType::fetch_service_types($mysqli);
    if (!empty($mysqli->error)) {
        throwError($mysqli, $mysqli->error, 503);
    }

    $serviceTypes = new stdClass();
    $serviceTypes->serviceTypes = $result;
    echo json_encode($serviceTypes);
    $mysqli->close();
}

function deleteService($data)
{
    $mysqli = getConn();
    $token = getallheaders()['token'];
    $user = User::auth($mysqli, $token);
    $id = $data->id;
    $service = Service::fetch_single_service($mysqli, $id);
    $service->delete_service($mysqli,  $user->email);
    if (!empty($mysqli->error)) {
        throwError($mysqli, $mysqli->error, 503);
    }
    $mysqli->close();
}


function addService($data)
{
    $mysqli = getConn();
    $token = getallheaders()['token'];
    $user = User::auth($mysqli, $token);
    $name = $data->name;
    $description = $data->description;
    $location = $data->location;
    $opening_hour = $data->openinghour;
    $closing_hour = $data->closinghour;
    $typeS = $data->typeservice;
    $pictures = $data->pictures;
    $service = new Service(null, $description, $name,  $location, $opening_hour, $closing_hour, $user->email, $typeS, $pictures);
    $service->add_service($mysqli, $name, $description, $location, $opening_hour, $closing_hour, $user->email, $typeS, $pictures);
    if (!empty($mysqli->error)) {
        throwError($mysqli, $mysqli->error, 503);
    }
    echo json_encode("");
    $mysqli->close();
}

function editService($data)
{
    $mysqli = getConn();
    $token = getallheaders()['token'];
    $user = User::auth($mysqli, $token);
    $id = $data->id;
    $service = Service::fetch_single_service($mysqli, $id);
    if (!isset($service)) {
        throwError($mysqli, "Service not found.", 404);
    }
    $query = "select * from save_the_date.serviceproviders where provider='" . $user->email . "'";
    $result = mysqli_query($mysqli, $query);
    if ($result->num_rows == 0) {
        throwError($mysqli, "You are not a service provider.", 405);
    }
    if ($service->provider != $user->email) {
        throwError($mysqli, "Unauthorized Acess.", 401);
    }
    $service->name = $data->name;
    $service->description = $data->description;
    $service->location = $data->location;
    $service->opening_hour = $data->openinghour;
    $service->closing_hour = $data->closinghour;
    $service->pictures = $data->pictures;

    $service->edit_service($mysqli);
    if (!empty($mysqli->error)) {
        throwError($mysqli, $mysqli->error, 503);
    }
    echo (json_encode("Done"));

    $mysqli->close();
}
