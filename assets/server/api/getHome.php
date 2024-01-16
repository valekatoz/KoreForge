<?php
$fileName = '../data/home.txt';

if (file_exists($fileName)) {
    $response = file_get_contents($fileName);
} else {
    $response = 'file does not exist';
}

header('Content-Type: text/plain');
echo ($response);
?>
