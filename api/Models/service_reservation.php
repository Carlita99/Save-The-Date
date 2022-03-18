<?php

class ServiceReservation
{
    public $id,
        $price,
        $date,
        $ide,
        $ids,
        $confirmed;
    function __construct(
        $id,
        $price,
        $date,
        $ide,
        $ids,
        $confirmed
    ) {
        $this->id = $id;
        $this->price = $price;
        $this->date = $date;
        $this->ide = $ide;
        $this->ids = $ids;
        $this->confirmed = $confirmed;
    }

    static function fetch_event_service_reservations($mysqli, $ide)
    {
        $query = "select * from save_the_date.servicereservations where ide ='" . $ide . "'";
        $result = mysqli_query($mysqli, $query);
        $arr = array();
        if ($result->num_rows != 0) {
            $result = mysqli_fetch_all($result, MYSQLI_ASSOC);
            foreach ($result as $res) {
                $arr[] = new ServiceReservation(
                    $res['id'],
                    $res['price'],
                    $res['date'],
                    $res['ide'],
                    $res['ids'],
                    $res['confirmed']
                );
            }
        }
        return $result;
    }

    static function fetch_service_reservations($mysqli, $serviceId)
    {
        $query = "select * from save_the_date.servicereservations where ids ='" . $serviceId . "'";
        $result = mysqli_query($mysqli, $query);
        $arr = array();
        if ($result->num_rows != 0) {
            $result = mysqli_fetch_all($result, MYSQLI_ASSOC);
            foreach ($result as $res) {
                if (!isset($res['confirmed']))

                    $res['confirmed'] = "";
                $arr[] = new ServiceReservation(
                    $res['id'],
                    $res['price'],
                    $res['date'],
                    $res['ide'],
                    $res['ids'],
                    $res['confirmed']
                );
            }
        }
        return $arr;
    }

    function addReservation($mysqli, $ide, $ids, $date, $price)
    {
        $query = "Insert into save_the_date.servicereservations (ide,ids,date,price,confirmed) values(" .
            "'" . $this->ide . "'," .
            "'" . $this->ids . "'," .
            "'" . $this->date . "'," .
            "0," .

            "null )";
        mysqli_query($mysqli, $query);
    }
    static function deleteByIds($mysqli, $ide, $ids)
    {
        $query = "DELETE from servicereservations WHERE ide='" . $ide . "' and ids='" . $ids . "';";
        mysqli_query($mysqli, $query);
    }

    static function editReservation($mysqli, $userEmail, $id, $date, $price, $confirmed)
    {
        $query = "Select * from servicereservations r,services s where r.id='" . $id . "' and r.ids=s.ids and s.provider='" . $userEmail . "'";
        $res = mysqli_query($mysqli, $query);
        if (empty($res)) {
            $query = "Select * from save_the_date.servicereservations r, events e, where r.id='" . $id . "' and r.ide=e.ide and e.organizer='" . $userEmail . "'";

            $res = mysqli_query($mysqli, $query);

            if (empty($res)) {
                throwError($mysqli, 403, "Unauthorized access");
            }
        }

        $reservation = mysqli_fetch_all($res, MYSQLI_ASSOC)[0];
        $date = date('Y-m-d');

        if ($date < $reservation['date']) {
            $ide = $reservation['ide'];
            $event = Event::fetchSingleEvent($mysqli, $ide);
            $event->total_cost += $price;

            $event->updateEvent(
                $mysqli,
                $userEmail
            );
            if ($mysqli->error) {
                echo json_encode($mysqli->error);
            }

            $query = "update save_the_date.servicereservations set " .
                "date = '" . $date . "' ," .
                "price = '" . $price . "' ," .
                "confirmed = '" . $confirmed . "' where id=' " . $id . "';";

            mysqli_query($mysqli, $query);


            $query = "select * from servicereservations where id='" . $id . "'";
            $res = mysqli_query($mysqli, $query);
            $res = mysqli_fetch_all($res, MYSQLI_ASSOC);
            echo (json_encode($res));
            return $res[0];
        } else throwError($mysqli, 403, 'You can\'t edit this reservation because event because it has already taken place');
    }


    static function delete_reservation($mysqli, $userEmail, $id)
    {
        $query = "Select * from servicereservations r,services s where r.id='" . $id . "' and r.ids=s.ids and s.provider='" . $userEmail . "'";
        $res = mysqli_query($mysqli, $query);
        if (mysqli_num_rows($res) == 0) {
            $query = "Select * from save_the_date.servicereservations r, events e, where r.id='" . $id . "' and r.ide=e.ide and e.organizer='" . $userEmail . "'";

            $res = mysqli_query($mysqli, $query);

            if (empty($res)) {
                throwError($mysqli, 403, "Unauthorized access");
            }
        }
        $reservation = mysqli_fetch_all($res, MYSQLI_ASSOC)[0];
        $date = date('Y-m-d');
        if ($date < $reservation['date']) {
            $event = Event::fetchSingleEvent($mysqli, $reservation['ide']);
            $event->total_cost -= $reservation['price'];

            $event->updateEvent(
                $mysqli,
                $userEmail
            );
            if ($mysqli->error) {
                echo json_encode($mysqli->error);
            }
            $query = "delete from save_the_date.servicereservations where id = '" . $id . "'";
            mysqli_query($mysqli, $query);
        } else throwError($mysqli, 403, 'You can\'t edit this reservation because event because it has already taken place');
    }
}
