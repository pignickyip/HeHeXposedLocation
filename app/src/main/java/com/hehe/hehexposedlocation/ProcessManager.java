package com.hehe.hehexposedlocation;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

/**
 * @author Jared Rummler
 * Copy from http://stackoverflow.com/questions/30619349/android-5-1-1-and-above-getrunningappprocesses-returns-my-application-packag
 */
public class ProcessManager {

    private static final String TAG = "ProcessManager";

    private static final String APP_ID_PATTERN;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // Android 4.2 (JB-MR1) changed the UID name of apps for multiple user account support.
            APP_ID_PATTERN = "u\\d+_a\\d+";
        } else {
            APP_ID_PATTERN = "app_\\d+";
        }
    }

    public static List<Process> getRunningProcesses() {
        List<Process> processes = new ArrayList<>();
        List<String> stdout = Shell.SH.run("toolbox ps -p -P -x -c");
        for (String line : stdout) {
            try {
                processes.add(new Process(line));
            } catch (Exception e) {
                android.util.Log.d(TAG, "Failed parsing line " + line);
            }
        }
        return processes;
    }

    public static List<Process> getRunningApps() {
        List<Process> processes = new ArrayList<>();
        List<String> stdout = Shell.SH.run("toolbox ps -p -P -x -c");
        int myPid = android.os.Process.myPid();
        for (String line : stdout) {
            try {
                Process process = new Process(line);
                if (process.user.matches(APP_ID_PATTERN)) {
                    if (process.ppid == myPid || process.name.equals("toolbox")) {
                        // skip the processes we created to get the running apps.
                        continue;
                    }
                    processes.add(process);
                }
            } catch (Exception e) {
                android.util.Log.d(TAG, "Failed parsing line " + line);
            }
        }
        return processes;
    }

    public static class Process implements Parcelable {

        /** User name */
        public final String user;

        /** User ID */
        public final int uid;

        /** Processes ID */
        public final int pid;

        /** Parent processes ID */
        public final int ppid;

        /** virtual memory size of the process in KiB (1024-byte units). */
        public final long vsize;

        /** resident set size, the non-swapped physical memory that a task has used (in kiloBytes). */
        public final long rss;

        public final int cpu;

        /** The priority */
        public final int priority;

        /** The priority, <a href="https://en.wikipedia.org/wiki/Nice_(Unix)">niceness</a> level */
        public final int niceness;

        /** Real time priority */
        public final int realTimePriority;

        /** 0 (sched_other), 1 (sched_fifo), and 2 (sched_rr). */
        public final int schedulingPolicy;

        /** The scheduling policy. Either "bg", "fg", "un", "er", or "" */
        public final String policy;

        /** address of the kernel function where the process is sleeping */
        public final String wchan;

        public final String pc;

        /**
         * Possible states:
         * <p/>
         * "D" uninterruptible sleep (usually IO)
         * <p/>
         * "R" running or runnable (on run queue)
         * <p/>
         * "S" interruptible sleep (waiting for an event to complete)
         * <p/>
         * "T" stopped, either by a job control signal or because it is being traced
         * <p/>
         * "W" paging (not valid since the 2.6.xx kernel)
         * <p/>
         * "X" dead (should never be seen)
         * </p>
         * "Z" defunct ("zombie") process, terminated but not reaped by its parent
         */
        public final String state;

        /** The process name */
        public final String name;

        /** user time in milliseconds */
        public final long userTime;

        /** system time in milliseconds */
        public final long systemTime;

        // Much dirty. Much ugly.
        private Process(String line) throws Exception {
            String[] fields = line.split("\\s+");
            user = fields[0];
            uid = android.os.Process.getUidForName(user);
            pid = Integer.parseInt(fields[1]);
            ppid = Integer.parseInt(fields[2]);
            vsize = Integer.parseInt(fields[3]) * 1024;
            rss = Integer.parseInt(fields[4]) * 1024;
            cpu = Integer.parseInt(fields[5]);
            priority = Integer.parseInt(fields[6]);
            niceness = Integer.parseInt(fields[7]);
            realTimePriority = Integer.parseInt(fields[8]);
            schedulingPolicy = Integer.parseInt(fields[9]);
            if (fields.length == 16) {
                policy = "";
                wchan = fields[10];
                pc = fields[11];
                state = fields[12];
                name = fields[13];
                userTime = Integer.parseInt(fields[14].split(":")[1].replace(",", "")) * 1000;
                systemTime = Integer.parseInt(fields[15].split(":")[1].replace(")", "")) * 1000;
            } else {
                policy = fields[10];
                wchan = fields[11];
                pc = fields[12];
                state = fields[13];
                name = fields[14];
                userTime = Integer.parseInt(fields[15].split(":")[1].replace(",", "")) * 1000;
                systemTime = Integer.parseInt(fields[16].split(":")[1].replace(")", "")) * 1000;
            }
        }

        private Process(Parcel in) {
            user = in.readString();
            uid = in.readInt();
            pid = in.readInt();
            ppid = in.readInt();
            vsize = in.readLong();
            rss = in.readLong();
            cpu = in.readInt();
            priority = in.readInt();
            niceness = in.readInt();
            realTimePriority = in.readInt();
            schedulingPolicy = in.readInt();
            policy = in.readString();
            wchan = in.readString();
            pc = in.readString();
            state = in.readString();
            name = in.readString();
            userTime = in.readLong();
            systemTime = in.readLong();
        }

        public String getPackageName() {
            if (!user.matches(APP_ID_PATTERN)) {
                // this process is not an application
                return null;
            } else if (name.contains(":")) {
                // background service running in another process than the main app process
                return name.split(":")[0];
            }
            return name;
        }

        public PackageInfo getPackageInfo(Context context, int flags)
                throws PackageManager.NameNotFoundException
        {
            String packageName = getPackageName();
            if (packageName == null) {
                throw new PackageManager.NameNotFoundException(name + " is not an application process");
            }
            return context.getPackageManager().getPackageInfo(packageName, flags);
        }

        public ApplicationInfo getApplicationInfo(Context context, int flags)
                throws PackageManager.NameNotFoundException
        {
            String packageName = getPackageName();
            if (packageName == null) {
                throw new PackageManager.NameNotFoundException(name + " is not an application process");
            }
            return context.getPackageManager().getApplicationInfo(packageName, flags);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(user);
            dest.writeInt(uid);
            dest.writeInt(pid);
            dest.writeInt(ppid);
            dest.writeLong(vsize);
            dest.writeLong(rss);
            dest.writeInt(cpu);
            dest.writeInt(priority);
            dest.writeInt(niceness);
            dest.writeInt(realTimePriority);
            dest.writeInt(schedulingPolicy);
            dest.writeString(policy);
            dest.writeString(wchan);
            dest.writeString(pc);
            dest.writeString(state);
            dest.writeString(name);
            dest.writeLong(userTime);
            dest.writeLong(systemTime);
        }

        public static final Creator<Process> CREATOR = new Creator<Process>() {

            public Process createFromParcel(Parcel source) {
                return new Process(source);
            }

            public Process[] newArray(int size) {
                return new Process[size];
            }
        };
    }

    /*
    Help finding a workaround (hack) to get the foreground app on Android 5.1.1+

    I wrote the following code to get the foreground app on Android 6.0 after reading Android source code.
    I have only tested it on my Nexus 9 and it seems to work.
    The code is messy (right now) but I really need someone to test it out.
    Copy and paste the following code into any class and call getForegroundApp():
    * */
    /** first app user */
    public static final int AID_APP = 10000;

    /** offset for uid ranges for each user */
    public static final int AID_USER = 100000;

    public static String getForegroundApp() {
        File[] files = new File("/proc").listFiles();
        int lowestOomScore = Integer.MAX_VALUE;
        String foregroundProcess = null;

        for (File file : files) {
            if (!file.isDirectory()) {
                continue;
            }

            int pid;
            try {
                pid = Integer.parseInt(file.getName());
            } catch (NumberFormatException e) {
                continue;
            }

            try {
                String cgroup = read(String.format("/proc/%d/cgroup", pid));

                String[] lines = cgroup.split("\n");

                if (lines.length != 2) {
                    continue;
                }

                String cpuSubsystem = lines[0];
                String cpuaccctSubsystem = lines[1];

                if (!cpuaccctSubsystem.endsWith(Integer.toString(pid))) {
                    // not an application process
                    continue;
                }

                if (cpuSubsystem.endsWith("bg_non_interactive")) {
                    // background policy
                    continue;
                }

                String cmdline = read(String.format("/proc/%d/cmdline", pid));

                if (cmdline.contains("com.android.systemui")) {
                    continue;
                }

                int uid = Integer.parseInt(
                        cpuaccctSubsystem.split(":")[2].split("/")[1].replace("uid_", ""));
                if (uid >= 1000 && uid <= 1038) {
                    // system process
                    continue;
                }

                int appId = uid - AID_APP;
                int userId = 0;
                // loop until we get the correct user id.
                // 100000 is the offset for each user.
                while (appId > AID_USER) {
                    appId -= AID_USER;
                    userId++;
                }

                if (appId < 0) {
                    continue;
                }

                // u{user_id}_a{app_id} is used on API 17+ for multiple user account support.
                // String uidName = String.format("u%d_a%d", userId, appId);

                File oomScoreAdj = new File(String.format("/proc/%d/oom_score_adj", pid));
                if (oomScoreAdj.canRead()) {
                    int oomAdj = Integer.parseInt(read(oomScoreAdj.getAbsolutePath()));
                    if (oomAdj != 0) {
                        continue;
                    }
                }

                int oomscore = Integer.parseInt(read(String.format("/proc/%d/oom_score", pid)));
                if (oomscore < lowestOomScore) {
                    lowestOomScore = oomscore;
                    foregroundProcess = cmdline;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return foregroundProcess;
    }

    private static String read(String path) throws IOException {
        StringBuilder output = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(path));
        output.append(reader.readLine());
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            output.append('\n').append(line);
        }
        reader.close();
        return output.toString();
    }
}