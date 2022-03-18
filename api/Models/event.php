<?php
class Event
{
    public
        $ide,
        $name,
        $number_of_guests,
        $date,
        $starting_hour,
        $duration,
        $description,
        $highlights,
        $total_cost,
        $organizer,
        $typeE,
        $pictures;
    function __construct(
        $ide,
        $name,
        $number_of_guests,
        $date,
        $starting_hour,
        $duration,
        $description,
        $highlights,
        $total_cost,
        $organizer,
        $typeE,
        $pictures
    ) {
        $this->ide = $ide;
        $this->name = $name;
        $this->number_of_guests = $number_of_guests;
        $this->date = $date;
        $this->starting_hour = $starting_hour;
        $this->duration = $duration;
        $this->description = $description;
        $this->highlights = $highlights;
        $this->total_cost = $total_cost;
        $this->organizer = $organizer;
        $this->typeE = $typeE;
        $this->pictures = $pictures;
    }

    static function fetchEvents($mysqli)
    {
        $all = mysqli_query($mysqli, 'Select * from save_the_date.events');
        $result = mysqli_fetch_all($all, MYSQLI_ASSOC);
        $arr = array();
        foreach ($result as $x) {

            $arr[] = new Event(
                $x['ide'],
                $x['name'],
                $x['number_of_guests'],
                $x['date'],
                $x['starting_hour'],
                $x['duration'],
                $x['description'],
                $x['highlights'],
                $x['total_cost'],
                $x['organizer'],
                $x['typeE'],
                json_decode($x['pictures'])
            );
        }
        return $arr;
    }
    static function fetchSingleEvent($mysqli, $id)
    {
        $event = Null;
        $result = mysqli_query($mysqli, 'Select * from save_the_date.events where ide=' . $id);
        if ($result->num_rows != 0) {
            $x = mysqli_fetch_all($result, MYSQLI_ASSOC)[0];
            $event = new Event(
                $x['ide'],
                $x['name'],
                $x['number_of_guests'],
                $x['date'],
                $x['starting_hour'],
                $x['duration'],
                $x['description'],
                $x['highlights'],
                $x['total_cost'],
                $x['organizer'],
                $x['typeE'],
                $x['pictures']
            );
        }
        return $event;
    }

    function addEventRecord($mysqli)
    {
        $query = 'Insert into save_the_date.events (name,number_of_guests,date,starting_Hour,duration,description,highlights,total_cost,organizer,pictures,typeE) values(' .
            "'" . $this->name . "'," .
            "'" . $this->number_of_guests . "'," .
            "'" . $this->date . "'," .
            "'" . $this->starting_hour . "'," .
            "'" . $this->duration . "'," .
            "'" . $this->description . "'," .
            "'" . $this->highlights . "'," .
            "'" . $this->total_cost . "'," .
            "'" . $this->organizer . "'," .
            "'" . $this->pictures . "'," .

            "'" . $this->typeE . "');";
        mysqli_query($mysqli, $query);
        $query = "select * from events order by ide desc limit 1";
        $result = mysqli_query($mysqli, $query);
        $result = mysqli_fetch_all($result, MYSQLI_ASSOC)[0];
        $event = new Event(
            $result['ide'],
            $result['name'],
            $result['number_of_guests'],
            $result['date'],
            $result['starting_hour'],
            $result['duration'],
            $result['description'],
            $result['highlights'],
            $result['total_cost'],
            $result['organizer'],
            $result['typeE'],
            $result['pictures']
        );
        return $event;
    }

    function updateEvent($mysqli, $userEmail)
    {

        if ($this->organizer != $userEmail) {
            throwError($mysqli, "Unauthorized Access.", 401);
        }
        $date = date('Y-m-d');
        if ($this->date > $date) {
            $query = "Update save_the_date.events " .
                "Set " .
                "name = '" . $this->name . "'," .
                "number_of_guests = '" . $this->number_of_guests . "'," .
                "date = '" . $this->date . "'," .
                "duration = '" . $this->duration . "'," .
                "starting_Hour = '" . $this->starting_hour . "'," .
                "typeE = '" . $this->typeE . "'," .
                "pictures = '" . $this->pictures . "'," .

                "description = '" . $this->description . "'," .
                "highlights = '" . $this->highlights . "'," .
                "total_cost = '" . $this->total_cost . "' " .
                "where ide = " . $this->ide;
            mysqli_query($mysqli, $query);
        } else throwError($mysqli, 403, 'You can\'t edit this event because it has already taken place');
    }

    function deleteEventRecord($mysqli)
    {



        $query = 'Delete from save_the_date.events where ide= ' . $this->ide;
        mysqli_query($mysqli, $query);
    }
}
