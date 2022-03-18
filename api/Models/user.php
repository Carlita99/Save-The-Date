<?php

class User
{

    public
        $email,
        $first_name,
        $last_name,
        $address,
        $phone_number,
        $birthday,
        $about,
        $profile_pic,
        $languages,
        $gender;
    private $password, $id;

    function __construct(
        $id,
        $email,
        $first_name,
        $last_name,
        $address,
        $phone_number,
        $birthday,
        $password,
        $about,
        $profile_pic,
        $languages,
        $gender
    ) {
        $this->id = $id;
        $this->email = $email;;
        $this->first_name = $first_name;
        $this->last_name = $last_name;
        $this->address = $address;
        $this->phone_number = $phone_number;
        $this->birthday = $birthday;
        $this->password = $password;
        $this->about = $about;
        $this->profile_pic = $profile_pic;
        $this->gender = $gender;
        $this->languages = $languages;
    }

    function getPassword()
    {
        return $this->password;
    }

    function getId()
    {
        return $this->id;
    }

    function add_user($mysqli, $email, $first_name, $last_name, $address, $phone_number, $birthday, $password, $about, $languages, $gender, $profile_pic)
    {
        $query = "Insert into save_the_date.users (email,first_name,last_name,address,phone_number,birthday,password,languages,gender,profile_pic,about) values(" .
            "'" . $this->email . "'," .
            "'" . $this->first_name . "'," .
            "'" . $this->last_name . "'," .
            "'" . $this->address . "'," .
            "'" . $this->phone_number . "'," .
            "'" . $this->birthday . "'," .
            "'" . $this->password . "'," .
            "'" . json_encode($this->languages) . "'," .
            "'" . $this->gender . "'," .

            "'" . $this->profile_pic . "'," .

            "'" . $this->about . "')";
        mysqli_query($mysqli, $query);
    }
    function findToken($mysqli)
    {

        $query = "Select token from save_the_date.tokens where email='" . $this->email . "'";
        $token = mysqli_query($mysqli, $query);

        if ($token->num_rows != 0) {
            $token = mysqli_fetch_object($token);
            return $token->token;
        } else return null;
    }


    static function fetch_user($mysqli, $email)
    {
        $query = "Select * from save_the_date.users where email ='" . $email . "';";
        $res = mysqli_query($mysqli, $query);
        $result = new stdClass();

        $result->user = mysqli_fetch_all($res, MYSQLI_ASSOC)[0];
        $query = "select organizer_name from save_the_date.eventorganizers where organizer='" . $email . "'";
        $orgname = mysqli_query($mysqli, $query);
        if (!isset($orgname))
            $orgname = mysqli_fetch_all($orgname, MYSQLI_ASSOC)[0];
        else
            $orgname = '';
        $result->organizer_name = $orgname;
        $query = "select provider_name from save_the_date.serviceproviders where provider='" . $email . "'";
        $provname = mysqli_query($mysqli, $query);
        if (!isset($provname))
            $provname = mysqli_fetch_all($provname, MYSQLI_ASSOC)[0];
        else $provname = '';
        $result->provname = $provname;
        return $result;
    }

    static function deleteToken($mysqli, $token)
    {

        $query = "Delete from save_the_date.tokens where token='" . $token . "'";
        mysqli_query($mysqli, $query);
    }


    function update_user($mysqli)
    {
        $query = 'Update save_the_date.users set ' .
            "first_name = '" . $this->first_name . "'," .
            "last_name = '" . $this->last_name . "'," .
            "address = '" . $this->address . "'," .
            "phone_number = '" . $this->phone_number . "'," .
            "birthday = '" . $this->birthday . "'," .
            "about = '" . $this->about . "'," .
            "languages = '" . json_encode($this->languages) . "'," .
            "gender = '" . $this->gender . "'," .
            "profile_pic = '" . $this->profile_pic . "' " .

            "where id='" . $this->getId() . "'";
        echo $this->getId();
        mysqli_query($mysqli, $query);
    }
    static function reset_password($mysqli, $email, $password)
    {
        $query = 'Update save_the_date.users set ' .
            "password = '" . $password . "'" .
            "where email='" . $email . "'";
        mysqli_query($mysqli, $query);
    }


    function getProvider($mysqli, $email)
    {
        $query = "SELECT provider_Name from serviceproviders where PROVIDER = '" . $this->email . "';";
        $res = mysqli_query($mysqli, $query);
        if ($res->num_rows != 0) {
            $res = mysqli_fetch_all($res, MYSQLI_ASSOC)[0];
            return $res;
        } else {
            throwError($mysqli, "No Service Provider Name", 404);
        }
    }
    function getOrganizer($mysqli, $email)
    {
        $query = "SELECT organizer_name from eventorganizers where organizer = '" . $this->email . "';";
        $res = mysqli_query($mysqli, $query);
        if ($res->num_rows != 0) {
            $res = mysqli_fetch_all($res, MYSQLI_ASSOC)[0];
            return $res;
        } else {
            throwError($mysqli, "No Event Organizer Name", 404);
        }
    }

    function setProvider($mysqli, $name)
    {
        $query = "Insert into serviceproviders (provider,provider_name) values(" .
            "'" . $this->email . "'," .
            "'" . $name . "');";
        mysqli_query($mysqli, $query);
    }

    function setOrganizer($mysqli, $name)
    {
        $query = "Insert into eventorganizers (organizer,organizer_name) values(" .
            "'" . $this->email . "'," .
            "'" . $name . "');";
        mysqli_query($mysqli, $query);
    }

    function saveToken($mysqli, $token)
    {

        $query = "Insert into save_the_date.tokens values (" .
            "'" . $token . "'," .
            "'" . $this->email . "')";
        mysqli_query($mysqli, $query);
    }


    static function auth($mysqli, $token)
    {
        $query = "Select * from tokens where token='" . $token . "'";
        $auth = mysqli_query($mysqli, $query);
        if ($auth->num_rows == 0) {

            $error = new stdClass();
            $error->message = "Please login to access this content!";
            echo json_encode($error);
            $mysqli->close();
            exit(0);
        }
        $auth = mysqli_fetch_object($auth);
        $query = "select * from save_the_date.users where email='" . $auth->email . "'";
        $auth = mysqli_query($mysqli, $query);
        $user = mysqli_fetch_all($auth, MYSQLI_ASSOC)[0];
        $user = new User($user['id'], $user['email'], $user['first_name'], $user['last_name'], $user['address'], $user['phone_number'], $user['birthday'], $user['password'], $user['about'], $user['profile_pic'], $user['languages'], $user['gender']);
        return $user;
    }

    static function find_user($mysqli, $email)
    {
        $query = "Select * from save_the_date.users u where u.email='" . $email . "'";
        $result = mysqli_query($mysqli, $query);

        $user = mysqli_fetch_all($result, MYSQLI_ASSOC)[0];
        $user = new User(null, $user['email'], $user['first_name'], $user['last_name'], $user['address'], $user['phone_number'], $user['birthday'], $user['password'], $user['about'], $user['profile_pic'], $user['languages'], $user['gender']);
        return $user;
    }
}
