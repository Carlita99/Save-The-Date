
<?php
class ServiceType
{
    public $type, $description;

    function __construct($type, $description)
    {
        $this->type = $type;
        $this->description = $description;
    }

    static function fetch_service_types($mysqli)
    {
        $query = 'Select * from save_the_date.servicetypes';
        $result = mysqli_query($mysqli, $query);
        $result = mysqli_fetch_all($result, MYSQLI_ASSOC);
        $arr = array();
        foreach ($result as $x) {
            $arr[] = new ServiceType($x['typeS'], $x['description']);
        }

        return $arr;
    }
}
