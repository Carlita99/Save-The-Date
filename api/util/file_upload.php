<?php
header('Access-Control-Allow-Origin: *');
header("Access-Control-Allow-Credentials: true");
header('Access-Control-Allow-Methods: GET, PUT, POST, DELETE, OPTIONS');
header('Access-Control-Max-Age: 1000');
header('Access-Control-Allow-Headers: Origin, Content-Type, X-Auth-Token , Authorization, token');

$json = file_get_contents('php://input');
$data = json_decode($json);
if (isset($_GET['f'])) {
	if (function_exists($_GET['f'])) {
		$_GET['f']($data);
	}
}

function saveFile($data)
{

	if (isset($data->base64string)) {
		$encoded_string = $data->base64string;
		$target_dir = 'D:/WampServer/www/save_the_date/uploads/'; // add the specific path to save the file
		$decoded_file = base64_decode(preg_replace('#^data:image/\w+;base64,#i', '', $encoded_string));
		$mime_type = finfo_buffer(finfo_open(), $decoded_file, FILEINFO_MIME_TYPE); // extract mime type
		$extension = "jpeg"; // extract extension from mime type
		$file = uniqid() . '.' . $extension; // rename file as a unique name
		$file_dir = $target_dir . $file;
		try {
			file_put_contents($file_dir, $decoded_file); // save
			echo json_encode('http://localhost:80/save_the_date/uploads/' . $file);
		} catch (Exception $e) {
			header('Content-Type: application/json');
			echo json_encode($e->getMessage());
		}
	}
}
