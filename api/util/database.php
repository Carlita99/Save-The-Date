<?php

function getConn()
{
    $mysqli = new mysqli('127.0.0.1', 'yorgo_wakim', '?yorgof16?', 'save_the_date');

    if ($mysqli->connect_error) {
        echo $mysqli->errno;
    }
    return $mysqli;
}
