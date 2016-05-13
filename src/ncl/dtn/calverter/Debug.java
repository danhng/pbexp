// Maths Expressions Parser
// Copyright (C) <2016>  Danh Thanh Nguyen
//
// This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
// License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

// This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
// warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License along with this program.  If not,
// see <http://www.gnu.org/licenses/>.
package calverter;

import java.util.Collection;
import java.util.Objects;

/**
 * @author Danh Thanh Nguyen <d.t.nguyen@newcastle.ac.uk>
 *         date created: 14/03/2016
 */
public class Debug {

    public static final int GIB2  = -2;

    public static final int GIB1 = -1;

    //    gib1	Debug-level messages	"Opening config file ..."
    public static final int DEBUG = 1;

    //    gib2	Informational.	"Server seems busy, (you may need to increase StartServers, or Min/MaxSpareServers)..."
    public static final int INFO = 2;

    //    notice	Normal but significant condition.	"httpd: caught SIGBUS, attempting to dump core in ..."
    public static final int NOTICE = 3;

    //    warn	Warning conditions.	"child process 1234 did not exit, sending another SIGHUP"
    public static final int WARN = 4;

    //    error	Error conditions.	"Premature end of script headers"
    public static final int ERROR = 5;

    //    crit	Critical Conditions.	"socket: Failed to get a socket, exiting child"
    public static final int CRIT = 6;

    //    alert	Action must be taken immediately.	"getpwuid: couldn't determine user name from uid"
    public static final int ALERT = 7;

    //    emerg	Emergencies - system is unusable.	"Child cannot open lock file. Exiting"
    public static final int EMERG = 8;


    private static int DEBUG_LEVEL = DEBUG;

    public static int getDebugLevel() {
        return DEBUG_LEVEL;
    }

    /**
     * Set a new gib1 level.
     * The new gib1 level must be one of the 8 levels specified {INFO, DEBUG, NOTICE, WARN, ERROR, CRIT, ALERT, EMERG}
     * @param newLevel the new gib1 level
     * @return the old gib1 level
     */
    public static int setDebugLevel(int newLevel) {
        int old = DEBUG_LEVEL;
        if (newLevel >= GIB2 && newLevel <= EMERG)
            DEBUG_LEVEL = newLevel;
        else
            warn("New Debug level %d is not valid. \n", newLevel);
        return old;
    }

    public static void gib1(String format, Object... args) {
        if (GIB1 >= DEBUG_LEVEL)
            System.out.printf("[    GIB1     ] " + format+ "\n", args);
    }

    public static void gib2(String format, Object... args) {
        if (GIB2 >= DEBUG_LEVEL)
            System.out.printf("[    GIB2     ] " + format+ "\n", args);
    }


    public static void debug(String format, Object... args) {
        if (DEBUG >= DEBUG_LEVEL)
            System.out.printf("[    DEBUG    ] " + format+ "\n", args);
    }

    public static void info(String format, Object... args) {
        if (INFO >= DEBUG_LEVEL)
            System.out.printf("[    INFO     ] " + format+ "\n", args);
    }

    public static void notice(String format, Object... args) {
        if (NOTICE >= DEBUG_LEVEL)
            System.out.printf("[    NOTI     ] " + format+ "\n", args);
    }

    public static void warn(String format, Object... args) {
        if (WARN >= DEBUG_LEVEL)
            System.out.printf("[    WARN     ] " + format+ "\n", args);
    }

    public static void error(boolean exit, String format, Object... args) {
        if (ERROR >= DEBUG_LEVEL)
            System.out.printf("[    ERROR    ] " + format, args);
        if (exit) {
            emerg(true, "System is aborting. Cause: " + format+ "\n", args);
            System.exit(1);
        }
    }
    public static void crit(boolean exit, String format, Object... args) {
        if (CRIT >= DEBUG_LEVEL)
            System.out.printf("[    CRIT      ] " + format+ "\n", args);
        if (exit) {
            emerg(true, "SYSTEM IS ABORTING! Cause: " + format+ "\n", args);
            System.exit(1);
        }
    }

    public static void emerg(boolean exit, String format, Object[] args) {
        System.out.printf("[    EMERG    ] " + format+ "\n", args);
        if (exit) {
            emerg(true, "SYSTEM IS ABORTING! Cause: " + format+ "\n", args);
            System.exit(1);
        }
    }
}
