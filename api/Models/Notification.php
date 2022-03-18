
<?php
class Notification
{
    public $id, $user, $dateNot, $title, $body, $status;

    function __construct($id, $user, $dateNot, $title, $body, $status)
    {
        $this->id = $id;
        $this->user = $user;
        $this->dateNot = $dateNot;
        $this->title = $title;
        $this->body = $body;
        $this->status = $status;
    }
}
