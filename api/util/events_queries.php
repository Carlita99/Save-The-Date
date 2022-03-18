<?php

function fetch_events_and_organizers($mysqli)
{
    $all = mysqli_query($mysqli, 'Select * from save_the_date.events');
    $events = mysqli_fetch_all($all, MYSQLI_ASSOC);

    $all = mysqli_query($mysqli, 'Select * from save_the_date.users,save_the_date.eventorganizers where email=organizer');
    $organizers = mysqli_fetch_all($all, MYSQLI_ASSOC);

    $arr = array();
    foreach ($events as $event) {
        $org = null;
        foreach ($organizers as $organizer) {
            if ($event['organizer'] == $organizer['email']) {
                $org = $organizer;
                break;
            }
        }
        $x = new stdclass();
        $x->event = $event;
        $x->organizer = $org;
        $arr[] = $x;
    }

    return $arr;
}
