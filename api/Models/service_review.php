<?php

class ServiceReview
{
    public $idsr,
        $description,
        $rating,
        $date,
        $ids,
        $organizer,
        $organizerName,
        $pictures;


    function __construct(
        $idsr,
        $description,
        $rating,
        $date,
        $ids,
        $organizer,
        $pictures,
        $organizerName

    ) {
        $this->idsr = $idsr;
        $this->description = $description;
        $this->rating = $rating;
        $this->ids = $ids;
        $this->organizer = $organizer;
        $this->pictures = $pictures;
        $this->date = $date;
        $this->organizerName = $organizerName;
    }


    static function fetch_service_reviews($mysqli, $serviceId)
    {
        $query = "Select s.*, o.organizer_name from save_the_date.servicereviews s, save_the_date.eventorganizers o where ids='" . $serviceId . "' and s.organizer = o.organizer";
        $result = mysqli_query($mysqli, $query);
        $result = mysqli_fetch_all($result, MYSQLI_ASSOC);
        $arr = array();
        foreach ($result as $rev) {
            $arr[] = new ServiceReview(
                $rev['idsr'],
                $rev['description'],
                $rev['rating'],
                $rev['date'],
                $rev['ids'],
                $rev['organizer'],
                $rev['pictures'],
                $rev['organizer_name']
            );
        }
        return $arr;
    }

    static function add_service_review($mysqli, $description, $rating, $date, $ids, $organizer, $pictures)
    {
        $query = "Insert into save_the_date.servicereviews (description,rating,date,ids,organizer,pictures) values (" .
            "'" . $description . "', " .
            "'" . $rating . "', " .
            "'" . $date . "', " .
            "'" . $ids . "', " .
            "'" . $organizer . "', " .
            "'" . json_encode($pictures) . "') ";
        mysqli_query($mysqli, $query);
    }

    static function delete_service_review($mysqli, $id, $email)
    {
        $query = "Select organizer from save_the_date.servicereviews where idsr = '" . $id . "'";
        $org = mysqli_query($mysqli, $query);
        if (empty($org)) {
            throwError($mysqli, "Review not found", 404);
        }
        $org = mysqli_fetch_all($org)[0][0];

        if ($email != $org) {
            throwError($mysqli, "Unauthorized Access", 401);
        }
        $query = "Delete from save_the_date.servicereviews where idsr = '" . $id . "'";
        mysqli_query($mysqli, $query);
    }

    static function update_service_review($mysqli, $idsr, $description, $rating, $date, $pictures, $email)
    {
        $query = "Select organizer from save_the_date.servicereviews where idsr = '" . $idsr . "'";
        $org = mysqli_query($mysqli, $query);
        if (empty($org)) {
            throwError($mysqli, "Review not found", 404);
        }
        $org = mysqli_fetch_all($org)[0][0];

        if ($email != $org) {
            throwError($mysqli, "Unauthorized Access", 401);
        }
        $query = "update save_the_date.servicereviews set  " .
            "description = '" . $description . "', " .
            "rating = '" . $rating . "', " .
            "date = '" . $date . "', " .
            "pictures = '" . json_encode($pictures) . "' " .
            "where idsr = '" . $idsr . "'";
        mysqli_query($mysqli, $query);
    }
}
