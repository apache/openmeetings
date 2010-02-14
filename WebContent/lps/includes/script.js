//=======================================================================
//                                                                       
// script.js
// by Antun Karlovac
//                                                                       
// Laszlo Tutorials include
//                                                                       
//=======================================================================

//* A_LZ_COPYRIGHT_BEGIN ******************************************************
//* Copyright 2001-2004 Laszlo Systems, Inc.  All Rights Reserved.            *
//* Use is subject to license terms.                                          *
//* A_LZ_COPYRIGHT_END ********************************************************

function getCurrentDate() {
    var now = new Date();
    var weekdays = new Array( 'Sunday',
                             'Monday', 
                             'Tuesday', 
                             'Wednesday', 
                             'Thursday', 
                             'Friday', 
                             'Saturday'
                            );
    var weekday = weekdays[now.getDay()];
    var months = new Array( 'January',
                            'February',
                            'March',
                            'April',
                            'May',
                            'June',
                            'July',
                            'August',
                            'September',
                            'October',
                            'November',
                            'December'
                           );
    var month = months[now.getMonth()];
    var year = now.getFullYear();
    var dateStr = weekday + ", " + month + " " + now.getDate();
    dateStr += ", " + year;
    dateStr = '<span class="todayDate">' + dateStr + '</span>';
    return dateStr;
}


