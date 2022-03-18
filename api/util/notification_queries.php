<?php

function add_notification($mysqli, $user, $dateNot, $title, $body, $status)
{
    $query = "insert into save_the_date.notifications (user, dateNot, title, body, status) values(" .
        "'" . $user . "'," .
        "'" . $dateNot . "'," .
        "'" . $title . "'," .
        "'" . $body . "'," .
        "'" . $status . "');";
        mysqli_query($mysqli, $query);
}

function edit_notification($mysqli, $user, $status)
{
    $query = "Update save_the_date.notifications set status = '" . $status . "' where user = '" . $user . "';";
    mysqli_query($mysqli, $query);
}

function get_notification($mysqli, $user)
{
	$query = "Select * from save_the_date.notifications where user = '" . $user . "';";
    $result = mysqli_query($mysqli, $query);
	$result = mysqli_fetch_all($result, MYSQLI_ASSOC);
    foreach ($result as $rev) {
        $res = $rev;
    }
    return $result;
}