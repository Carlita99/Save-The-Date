<?php
class Service
{
    public $ids,
        $description,
        $name,
        $location,
        $opening_hour,
        $closing_hour,
        $provider,
        $typeS,
        $pictures;

    function __construct(
        $ids,
        $description,
        $name,
        $location,
        $opening_hour,
        $closing_hour,
        $provider,
        $typeS,
        $pictures
    ) {
        $this->ids = $ids;
        $this->description = $description;
        $this->name = $name;
        $this->location = $location;
        $this->opening_hour = $opening_hour;
        $this->closing_hour = $closing_hour;
        $this->provider = $provider;
        $this->typeS = $typeS;
        $this->pictures = $pictures;
    }

    static function fetch_services($mysqli)
    {
        $query = 'Select * from save_the_date.services';
        $result = mysqli_query($mysqli, $query);
        $result = mysqli_fetch_all($result, MYSQLI_ASSOC);
        $arr = array();
        foreach ($result as $x) {
            $arr[] = new Service(
                $x['ids'],
                $x['description'],
                $x['name'],
                $x['location'],
                $x['opening_hour'],
                $x['closing_hour'],
                $x['provider'],
                $x['typeS'],
                $x['pictures']
            );
        }
        return $arr;
    }

    function delete_service($mysqli, $userEmail)
    {
        $query = "select * from save_the_date.serviceproviders where provider='" . $userEmail . "'";
        $result = mysqli_query($mysqli, $query);
        if ($result->num_rows == 0) {
            throwError($mysqli, "You are not a service provider.", 405);
        }
        $query = "select * from save_the_date.services where ids = '" . $this->ids . "'";
        $result = mysqli_query($mysqli, $query);
        if ($result->num_rows == 0) {
            throwError($mysqli, "Service not Found.", 404);
        }
        $result = mysqli_fetch_object($result);
        if ($result->provider != $userEmail) {
            throwError($mysqli, "Unauthorized Access.", 401);
        }
        $query = "Delete from save_the_date.services where ids='" . $this->ids . "'";
        mysqli_query($mysqli, $query);
        return;
    }

    static function fetch_single_service($mysqli, $id)
    {
        $query = "Select * from save_the_date.services where ids='" . $id . "'";
        $result = mysqli_query($mysqli, $query);
        $result = mysqli_fetch_all($result, MYSQLI_ASSOC)[0];
        if (empty($result))
            return null;
        $result = new Service(
            $result['ids'],
            $result['description'],
            $result['name'],
            $result['location'],
            $result['opening_hour'],
            $result['closing_hour'],
            $result['provider'],
            $result['typeS'],
            $result['pictures']
        );
        return $result;
    }

    function edit_service($mysqli)
    {

        $query = "select * from save_the_date.services where ids = '" . $this->ids . "'";
        $result = mysqli_query($mysqli, $query);
        if ($result->num_rows == 0) {
            throwError($mysqli, "Event not Found.", 404);
        }


        $query = "Update save_the_date.services set " .
            "name = '" . $this->name . "'," .
            "description = '" . $this->description . "'," .
            "location = '" . $this->location . "'," .
            "opening_hour = '" . $this->opening_hour . "'," .
            "closing_hour = '" . $this->closing_hour . "', " .
            "pictures = '" . $this->pictures . "' " .

            "where ids='" . $this->ids . "' ";
        mysqli_query($mysqli, $query);
    }


    function add_service($mysqli, $name, $description, $location, $opening_hour, $closing_hour, $userEmail, $typeS, $pictures)
    {
        $query = "select * from save_the_date.serviceproviders where provider='" . $this->provider . "'";
        $result = mysqli_query($mysqli, $query);
        if ($result->num_rows == 0) {
            throwError($mysqli, "You are not a service provider.", 405);
        }
        $query = "Insert into save_the_date.services (name,description,location,opening_hour,closing_hour,typeS,pictures,provider) values(" .
            "'" . $this->name . "'," .
            "'" . $this->description . "'," .
            "'" . $this->location . "'," .
            "'" . $this->opening_hour . "'," .
            "'" . $this->closing_hour . "'," .
            "'" . $this->typeS . "'," .
            "'" . $this->pictures . "'," .

            "'" . $this->provider . "')";

        mysqli_query($mysqli, $query);
    }
}
