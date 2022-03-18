<?php

function fetch_services_and_providers($mysqli)
{
    $all = mysqli_query($mysqli, 'Select * from save_the_date.services');
    $services = mysqli_fetch_all($all, MYSQLI_ASSOC);

    $all = mysqli_query($mysqli, 'Select * from save_the_date.users,save_the_date.serviceproviders where email=provider');
    $providers = mysqli_fetch_all($all, MYSQLI_ASSOC);

    $arr = array();
    foreach ($services as $service) {
        $pro = null;
        foreach ($providers as $provider) {
            if ($service['provider'] == $provider['email']) {
                $pro = $provider;
                break;
            }
        }
        $x = new stdclass();
        $x->service = $service;
        $x->provider = $pro;
        $arr[] = $x;
    }

    return $arr;
}
