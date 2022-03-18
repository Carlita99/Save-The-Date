<?php
header('Access-Control-Allow-Origin: *');
header("Access-Control-Allow-Credentials: true");
header('Access-Control-Allow-Methods: GET, PUT, POST, DELETE, OPTIONS');
header('Access-Control-Max-Age: 1000');
header('Access-Control-Allow-Headers: Origin, Content-Type, X-Auth-Token , Authorization, token');
include('../util/database.php');
include('../util/error_handling.php');
include('../Models/user.php');

include('../util/authentication.php');
$json = file_get_contents('php://input');
$data = json_decode($json);
$data = json_decode($json);

if (isset($_GET['f'])) {
    if (function_exists($_GET['f'])) {
        $_GET['f']($data);
    }
}

function setOrganizerName($data)
{
    $headers = getallheaders();
    $mysqli = getConn();
    if (!isset($headers['token'])) {
        throwError($mysqli, 'Please login', 401);
    } else {
        $token = $headers['token'];
    }
    if (isset($token)) {
        $user = User::auth($mysqli, $token);
        if (isset($user)) {


            $user->setOrganizer($mysqli, $data->organizer_name);
            if (!empty($mysqli->error)) {
                throwError($mysqli, $mysqli->error, 503);
            }
            $mysqli->close();
        }
    }
}


function getEventOrganizerName($data)
{

    $headers = getallheaders();
    $mysqli = getConn();
    if (!isset($headers['token'])) {
        throwError($mysqli, 'Please login', 401);
    } else {
        $token = $headers['token'];
    }
    if (isset($token)) {
        $user = User::auth($mysqli, $token);
        if (isset($user)) {
            $res = $user->getOrganizer($mysqli, $user->email);
            if (!empty($mysqli->error)) {
                throwError($mysqli, $mysqli->error, 503);
            }
            echo json_encode($res);
            $mysqli->close();
        }
    }
}


function signup($data)
{
    $mysqli = getConn();
    $email = $data->user->email;
    $first_name = $data->user->first_name;
    $last_name = $data->user->last_name;
    $address = $data->user->address;
    $phone_number = $data->user->phone_number;
    $birthday = $data->user->birthday;
    $password = $data->user->password;
    $about = $data->user->about;
    $languages = $data->user->languages;
    $gender = $data->user->gender;
    $profile_pic = $data->user->profile_pic;
    $password = password_hash($password, PASSWORD_DEFAULT);
    $user = new User(null, $email, $first_name, $last_name, $address, $phone_number, $birthday, $password, $about, $profile_pic, $languages, $gender);
    $user->add_user($mysqli, $email, $first_name, $last_name, $address, $phone_number, $birthday, $password, $about, $languages, $gender, $profile_pic);
    if (!empty($mysqli->error)) {
        throwError($mysqli, $mysqli->error, 503);
    }
    $mysqli->close();
    return;
}

function getProviderName($data)
{
    $headers = getallheaders();
    $mysqli = getConn();
    if (!isset($headers['token'])) {
        throwError($mysqli, 'Please login', 401);
    } else {
        $token = $headers['token'];
    }
    if (isset($token)) {
        $user = User::auth($mysqli, $token);
        if (isset($user)) {
            $res = $user->getProvider($mysqli, $user->email);
            if (!empty($mysqli->error)) {
                throwError($mysqli, $mysqli->error, 503);
            }
            echo json_encode($res);
            $mysqli->close();
        }
    }
}

function setProviderName($data)
{
    $headers = getallheaders();
    $mysqli = getConn();
    if (!isset($headers['token'])) {
        throwError($mysqli, 'Please login', 401);
    } else {
        $token = $headers['token'];
    }
    if (isset($token)) {
        $user = User::auth($mysqli, $token);
        if (isset($user)) {


            $user->setProvider($mysqli, $data->provider_name);
            if (!empty($mysqli->error)) {
                throwError($mysqli, $mysqli->error, 503);
            }
            $mysqli->close();
        }
    }
}

function login($data)
{
    $mysqli = getConn();
    $email = $data->email;
    $password = $data->password;
    $user = User::find_user($mysqli, $email);
    if (!isset($user)) {
        throwError($mysqli, "User not found.", 404);
    }


    if (password_verify($password, $user->getPassword())) {
        $token = $user->findToken($mysqli);
        if ($token == null) {
            $token = generateToken();
            $user->saveToken($mysqli, $token);
        }
        $authData = new stdClass();
        $authData->token = $token;
        if (!empty($mysqli->error)) {
            throwError($mysqli, $mysqli->error, 503);
        }
        $authData->user = $user;
        echo json_encode($authData);
    } else {
        throwError($mysqli, "Wrong Password.", 403);
    }
    $mysqli->close();
}

function logout($data)
{
    $mysqli = getConn();
    $token = $data->token;
    User::deleteToken($mysqli, $token);
    if (!empty($mysqli->error)) {
        throwError($mysqli, $mysqli->error, 503);
    }
    $mysqli->close();
}
function forgotPassword($data)
{
    $mysqli = getConn();
    $newpass = $data->password;
    $email = $data->email;
    $newpass = password_hash($newpass, PASSWORD_DEFAULT);
    User::reset_password($mysqli, $email, $newpass);

    if (!empty($mysqli->error)) {
        throwError($mysqli, $mysqli->error, 503);
    }
    $mysqli->close();
}
function changePassword($data)
{
    $mysqli = getConn();

    $newpass = $data->newpassword;
    $oldpass = $data->oldpassword;
    $email = $data->email;
    $user = User::find_user($mysqli, $email);


    if (!empty($mysqli->error)) {
        throwError($mysqli, $mysqli->error, 503);
    }
    if (!password_verify($oldpass, $user->getPassword())) {
        throwError($mysqli, "Old password is Incorrect.", 403);
    }
    $newpass = password_hash($newpass, PASSWORD_DEFAULT);
    User::reset_password($mysqli, $email, $newpass);
    if (!empty($mysqli->error)) {
        throwError($mysqli, $mysqli->error, 503);
    }
    $mysqli->close();
}


function editUser($data)
{
    $mysqli = getConn();
    $token = getallheaders()['token'];
    $user = User::auth($mysqli, $token);
    $id = $user->getId();
    $user->first_name = $data->user->first_name;
    $user->last_name = $data->user->last_name;
    $user->address = $data->user->address;
    $user->phone_number = $data->user->phone_number;
    $user->birthday = $data->user->birthday;
    $user->about = $data->user->about;
    $user->languages = $data->user->languages;
    $user->gender = $data->user->gender;
    $user->profile_pic = $data->user->profile_pic;

    $user->update_user($mysqli);
    if (!empty($mysqli->error)) {
        throwError($mysqli, $mysqli->error, 503);
    }
    $user = User::auth($mysqli, $token);
    // $user = new User(
    //     $user->id,
    //     $user->email,
    //     $user->first_name,
    //     $user->last_name,
    //     $user->address,
    //     $user->phone_number,
    //     $user->birthday,
    //     $user->password,
    //     $user->about,
    //     $user->profile_pic,
    //     $user->languages,
    //     $user->gender
    // );
    $response = new stdClass();
    $response->user = $user;
    echo json_encode($response);
    $mysqli->close();
    return;
}

function getUser($data)
{
    $mysqli = getConn();
    $id = $data->email;
    $result = User::fetch_user($mysqli, $id);
    if (!empty($mysqli->error)) {
        throwError($mysqli, $mysqli->error, 503);
    }
    echo json_encode($result);
    $mysqli->close();
}
