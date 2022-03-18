<?php

function throwError($mysqli, $message, $code)
{
    $response = new stdClass();
    $response->error = new stdClass();
    $response->error->message = $message;
    $response->code = $code;
    echo json_encode($response);
    $mysqli->close();
    exit(0);
}

// Response codes Guide:
// Errors:
// Server Errors (5xx):
// 503: Database (usually query) Error
// User error (4xx):
// 401: Unauthorized Access
// 402: Date error
// 403: wrong password
// 404: Requested item/page not found
// 405: Not a Service Provider
// 406: Not an Event Handler

//Successful Requests:
