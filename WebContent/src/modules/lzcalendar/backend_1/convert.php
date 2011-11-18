<?php

// PHP iCal to RDF script
// Created May 2006 for Laszlo Systems, Inc. by J Crowley

$filename = 'caldata/' . $_GET['filename'];
$handle = fopen($filename, "r");

echo "<rdf:RDF\n";
echo "xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'\n";
echo "xmlns='http://www.w3.org/2002/12/cal/ical#'\n";
echo "xmlns:i='http://www.w3.org/2002/12/cal/ical#'\n";
echo "xmlns:x='http://www.w3.org/2002/12/cal/prod/Apple_Comp_628d9d8459c556fa#'>\n";

while(!feof($handle)){
	$buffer = fgets($handle, 4096);
	$bufferbreak = explode(":", $buffer);
	$tagbreak = $bufferbreak[0];
	$attrbreak = $bufferbreak[1];
	if($tagbreak == ''){
		$curtag = strtolower($bufferbreak[0]);
	} else {
		$curtag = strtolower($tagbreak);
	}
	if($curbreak_attrbreak[1] == ''){
		$curattr = $bufferbreak[1];
	} else {
		$curattr = $attrbreak;
	}

	$semicolonremover = explode(";", $curtag);
	if($semicolonremover[1] != ''){
		$curtag = $semicolonremover[0];
	}

	$curtag = str_replace(chr(10), "", $curtag);
	$curtag = str_replace(chr(13), "", $curtag);
	$curattr = str_replace(chr(10), "", $curattr);
	$curattr = str_replace(chr(13), "", $curattr);

	if(substr($curattr, 0, 9) == "VCALENDAR"){
		$curattr = "Vcalendar";
	}
	if(substr($curattr, 0, 6) == "VEVENT"){
		$curattr = "Vevent";
	}

	if(substr($curtag, 0, 4) == "x-wr"){
		$curtag = "x:wr" . ucwords(substr($curtag, 5));
	}

	if($curtag == ''){
		echo '';
	} else if($curtag == 'begin'){
		if($curattr == 'Vevent'){
			echo "<component>\n";
		}
		echo "<" . $curattr . ">\n";
	} else if($curtag == 'end'){
		echo "</" . $curattr . ">\n";
		if($curattr == 'Vevent'){
			echo "</component>\n";
		}
	} else if($curtag == 'dtstamp' || $curtag == 'dtstart' || $curtag == 'dtend'){
		$curattrtemp = substr($curattr, 0, 4) . "-" . substr($curattr, 4, 2) . "-" . substr($curattr, 6, 5);
		if(substr($curattr, 11, 2) != ''){
			$curattrtemp = $curattrtemp . ":" . substr($curattr, 11, 2) . ":" . substr($curattr, 13);
		}
		$curattr = $curattrtemp;
		echo "<" . $curtag . " rdf:parseType='Resource'>";
		echo "<dateTime>" . $curattr . "</dateTime>";
		echo "</" . $curtag . ">\n";

	} else {
		echo "<" . $curtag . ">" . $curattr . "</" . $curtag . ">\n";
	}
}
fclose($handle);

echo "</rdf:RDF>";

?>