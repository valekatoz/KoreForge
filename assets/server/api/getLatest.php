<?php
$fileName = '../data/latest/Kore-latest.jar';

if (file_exists($fileName)) {
    header('Content-Type: application/java-archive');
    header('Content-Disposition: attachment; filename="' . basename($fileName) . '"');
    header('Content-Length: ' . filesize($fileName));

    readfile($fileName);
} else {
    echo 'File does not exist';
}
?>
