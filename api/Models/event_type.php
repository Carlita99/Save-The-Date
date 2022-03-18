<?php
class EventType
{
    public $type;
    public $description;

    function __construct($t, $d)
    {
        $this->type = $t;
        $this->description = $d;
    }
    static function fetchEventTypes($mysqli)
    {
        $all = mysqli_query($mysqli, 'Select * from save_the_date.eventtypes');
        $result = mysqli_fetch_all($all, MYSQLI_ASSOC);
        $arr = array();
        foreach ($result as $x) {
            $arr[] = new EventType($x['typeE'], $x['description']);
        }
        return $arr;
    }
}
