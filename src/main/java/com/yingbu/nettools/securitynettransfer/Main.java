package com.yingbu.nettools.securitynettransfer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;

/**
 * Created by Yingbu on 16/07/2017.
 * Main function
 */
public class Main {
    public static void main(String[] args) {
        CommandLine cli = parseCommandLine(args);
    }

    private static CommandLine parseCommandLine(String[] args){
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = null;
        Option help = new Option("h","help",false,"Print this message.");
        return null;
    }
}
