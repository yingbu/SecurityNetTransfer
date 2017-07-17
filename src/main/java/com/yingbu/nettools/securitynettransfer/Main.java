package com.yingbu.nettools.securitynettransfer;

import org.apache.commons.cli.*;

/**
 * Created by Yingbu on 16/07/2017.
 * Main function
 */
public class Main {
    public static void main(String[] args) {
        CommandLine cli = parseCommandLine(args);

    }

    private static CommandLine parseCommandLine(String[] args){
        HelpFormatter formatter = new HelpFormatter();
        String formatstr = "java -jar SNT.jar [client|server] [options...]";

        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = null;
        Option help = new Option("h","help",false,"Print this message.");
        Option server = new Option("ms","server",false,"SNT used server mode.");
        Option client = new Option("mc","client",false,"SNT used client mode.");
        //server option

        //client option
        Option clientlisten = new Option("","serverhost",true,"Set the SNT listen port.");
        Option clientport = new Option("","serverhost",true,"Set the SNT listen port.");
        Option clientpass = new Option("","serverhost",true,"Set the SNT listen port.");

        Options options = new Options();
        options.addOption(help);
        options.addOption(server);
        options.addOption(client);

        try {
            commandLine = parser.parse(options, args);
        } catch (ParseException e) {
            e.printStackTrace();
            formatter.printHelp( formatstr, options);
        }
        if(commandLine != null && commandLine.getOptions().length > 0){
            if(commandLine.hasOption("h")){
                formatter.printHelp( formatstr, options);
            }else if(commandLine.hasOption("server")){
                System.out.println("server mode run.");

            }else if(commandLine.hasOption("client")){
                System.out.println("client mode run.");
            }
        }else{
            formatter.printHelp( formatstr, options);
        }
        return commandLine;
    }
}
