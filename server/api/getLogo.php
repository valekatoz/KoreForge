<?php
$fileName = '../data/logo.png';

if (file_exists($fileName)) {
    $response = file_get_contents($fileName);
} else {
    $response = 'file does not exist';
}

header('Content-Type: image/png');
echo ($response);
?>
