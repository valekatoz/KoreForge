<?php
include '../db/db.php';

if (isset($_GET['key']) && !empty($_GET['key'])) {
    $uuid = base64_decode($_GET['key']);

    if ($uuid !== false) {
        $stmt = $conn->prepare("SELECT * FROM users WHERE uuid = ?");
        $stmt->bind_param("s", $uuid);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result->num_rows > 0) {
            $response = array("status" => "success", "message" => "key is valid");
        } else {
            $response = array("status" => "error", "message" => "key is not valid");
        }

        $stmt->close();
    } else {
        $response = array("status" => "error", "message" => "error occurred parsing the key");
    }
} else {
    $response = array("status" => "error", "message" => "key is missing");
}

header('Content-Type: application/json');
echo json_encode($response);
?>
