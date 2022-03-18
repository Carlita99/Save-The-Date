<?php

class EventReview
{
    public $ider,
        $comment,
        $rating,
        $reviewer_fname,
        $reviewer_lname,
        $ide,
        $pictures,
        $user;


    function __construct(
        $ider,
        $comment,
        $rating,
        $reviewer_fname,
        $reviewer_lname,
        $ide,
        $pictures,
        $user

    ) {
        $this->ider = $ider;
        $this->comment = $comment;
        $this->rating = $rating;
        $this->reviewer_fname = $reviewer_fname;
        $this->reviewer_lname = $reviewer_lname;
        $this->ide = $ide;
        $this->pictures = $pictures;
        $this->user = $user;
    }


    static function fetch_event_reviews($mysqli, $eventId)
    {
        $query = "Select e.*, u.first_name, u.last_name from save_the_date.eventreviews e, save_the_date.users u where ide='" . $eventId . "' and e.user=u.email";
        $result = mysqli_query($mysqli, $query);
        $result = mysqli_fetch_all($result, MYSQLI_ASSOC);
        $arr = array();
        foreach ($result as $rev) {
            $arr[] = new EventReview(
                $rev['ider'],
                $rev['comment'],
                $rev['rating'],
                $rev['reviewer_fname'],
                $rev['reviewer_lname'],
                $rev['ide'],
                $rev['pictures'],
                $rev['user']
            );
        }

        return $arr;
    }


    static function delete_event_review($mysqli, $id, $email)
    {
        $query = "Select user from save_the_date.eventreviews where ider = '" . $id . "'";
        $org = mysqli_query($mysqli, $query);
        if (empty($org)) {
            throwError($mysqli, "Review not found", 404);
        }
        $org = mysqli_fetch_all($org)[0][0];

        if ($email != $org) {
            throwError($mysqli, "Unauthorized Access", 401);
        }
        $query = "Delete from save_the_date.eventreviews where ider = '" . $id . "'";
        mysqli_query($mysqli, $query);
    }



    static function add_event_review($mysqli, $comment, $rating, $reviewer_fname, $reviewer_lname, $ide, $pictures, $userEmail)
    {
        $query = "Insert into save_the_date.eventreviews (comment,rating,reviewer_fname,reviewer_lname,ide,pictures,user) values (" .
            "'" . $comment . "', " .
            "'" . $rating . "', " .
            "'" . $reviewer_fname . "', " .
            "'" . $reviewer_lname . "', " .
            "'" . $ide . "', " .
            "'" . json_encode($pictures) . "', " .
            "'" . $userEmail . "') ";
        mysqli_query($mysqli, $query);
    }

    static function update_event_review($mysqli, $ider, $comment, $rating, $pictures, $email)
    {
        $query = "Select user from save_the_date.eventreviews where ider = '" . $ider . "'";
        $userEmail = mysqli_query($mysqli, $query);
        if (empty($userEmail)) {
            throwError($mysqli, "Review not found", 404);
        }
        $userEmail = mysqli_fetch_all($userEmail)[0][0];
        if ($email != $userEmail) {
            throwError($mysqli, "Unauthorized Access", 401);
        }
        $query = "update save_the_date.eventreviews set  " .
            "comment = '" . $comment . "', " .
            "rating = '" . $rating . "', " .
            "pictures = '" . json_encode($pictures) . "' " .

            "where ider = '" . $ider . "'";
        echo $query;
        mysqli_query($mysqli, $query);
    }
}
