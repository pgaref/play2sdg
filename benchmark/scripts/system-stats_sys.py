#!/usr/bin/env python


"""
Gathers system load statistics on memory, cpu, and network throughput
per second and outputs it to a .csv file.

"""

import os
import sys
import time
import psutil
import optparse
import re
import subprocess
from datetime import datetime, timedelta

if os.name != 'posix':
    sys.exit('platform not supported')
    
parser = optparse.OptionParser()
parser.add_option("-f", dest="filename", help="the name of the output CSV file", metavar="FILE")
parser.add_option("-i", dest="interface", help="the name of the network interface to be monitored (e.g. eth0)", metavar="INTERFACE")

(options, args) = parser.parse_args()

def main():
    try:
        cpus = psutil.cpu_percent(interval=0,percpu=True)
        header=[]
	cpu_sum = []
        for i in range(0, len(cpus)):
            header.append('Core-' + str(i) + '_usage_percent')
        
        f = open(options.filename, 'w')
        f.write('Total_CPU_usage_percent,Memory_usage_percent,Throughput_in_Mbps,Throughput_out_Mbps,Total_CPU_usage_iowait_percent' + ',' + ",".join(header) + "," + "Read_speed_MBps" + "," + "Write_speed_MBps" + "," + "Avg_CPU_usage"+'\n')
        interval = 1
        bytes_received_prev, bytes_sent_prev = get_network_bytes(options.interface)
	bytes_read_prev = psutil.disk_io_counters(perdisk=False).read_bytes
	bytes_written_prev = psutil.disk_io_counters(perdisk=False).write_bytes
        prev_time = time.time()
        time.sleep(interval)
        
        while 1:
            cpu = str(psutil.cpu_percent(interval=0))
            mem = str(psutil.virtual_memory().percent)
            cpus = psutil.cpu_percent(interval=0,percpu=True)
	    cpu_iowait = str(psutil.cpu_times_percent(interval=0,percpu=False).iowait)
            bytes_received_curr, bytes_sent_curr = get_network_bytes(options.interface)
	    bytes_read_curr = psutil.disk_io_counters(perdisk=False).read_bytes
	    bytes_written_curr = psutil.disk_io_counters(perdisk=False).write_bytes

            # calculate elapsed time
            curr_time = time.time()
            elapsed_time = curr_time - prev_time
            prev_time = curr_time;

	    # calculate network throughput
            throughput_in = (((bytes_received_curr - bytes_received_prev) * 8.0) / elapsed_time) / 1000000.0
            throughput_out = (((bytes_sent_curr - bytes_sent_prev) * 8.0) / elapsed_time) / 1000000.0
	    
	    # calculate disk throughput
	    disk_io_read = ((float)  (bytes_read_curr - bytes_read_prev) / (1024.0 * 1024.0)) / elapsed_time
	    disk_io_write = ((float) (bytes_written_curr - bytes_written_prev) / (1024.0 * 1024.0)) / elapsed_time

	    # update variables
            bytes_received_prev = bytes_received_curr
            bytes_sent_prev = bytes_sent_curr
	    bytes_read_prev = bytes_read_curr
	    bytes_written_prev = bytes_written_curr

	    # round throughputs to 5 decimals
            incoming = str(round(throughput_in,5))
            outgoing = str(round(throughput_out,5))
	    read = str(round(disk_io_read,5))
	    write = str(round(disk_io_write,5))
	    cpu_sum.append(float(cpu))
	    cpu_avg = str(round(sum(cpu_sum)/float(len(cpu_sum)),5))

	    # append record to csv file
            f.write(cpu + ',' + mem + ',' + incoming + ',' + outgoing + ',' + cpu_iowait + ',' + ",".join(map(str,cpus)) + "," + read + "," + write + "," + cpu_avg + '\n')
            f.flush()
            time.sleep(interval)
    except (KeyboardInterrupt, SystemExit):
        print "Exiting."
        pass

def get_network_bytes(interface):
    rx_bytes = open("/sys/class/net/" + interface + "/statistics/rx_bytes", "r").read().rstrip()
    tx_bytes = open("/sys/class/net/" + interface + "/statistics/tx_bytes", "r").read().rstrip()
    return (long(rx_bytes), long(tx_bytes))

if __name__ == '__main__':
    main()
