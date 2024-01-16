<?php
  $servername = "localhost";
  $username = "root";
  $password = "password";
  $dbname = "Kore";
  
  $conn = new mysqli($servername, $username, $password, $dbname);
  
  if ($conn->connect_error) {
      die("Connessione al database fallita: " . $conn->connect_error);
  }
?>
