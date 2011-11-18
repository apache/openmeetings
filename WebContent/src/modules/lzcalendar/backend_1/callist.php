<?php

$clist = scandir('caldata');

echo "<calendars>";
for($i = 2; $i <= sizeof($clist); $i++){
	if($clist[$i] != ''){
		echo "<cal>" . $clist[$i] . "</cal>";
	}
}

echo "</calendars>";

?>
