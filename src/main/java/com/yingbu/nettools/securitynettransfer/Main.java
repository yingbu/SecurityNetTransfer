package com.yingbu.nettools.securitynettransfer;

import com.yingbu.nettools.securitynettransfer.util.ThreadPool;
import org.apache.commons.cli.*;

/**
 * Created by Yingbu on 16/07/2017.
 * Main function
 */
public class Main {
    private static int server_mode = 1; //0:server 1:client

    private static int adv_servicepool_size = 32;
    private static int adv_monitor = 0;

    private static int ms_transferlisten_port;
    private static String ms_transferhost;
    private static int ms_transferport;

    private static String mc_serverhost;
    private static int mc_serverport;
    private static int mc_listen;

    private static String com_password;

    public static void main(String[] args) {
        parseCommandLine(args);
        ThreadPool.initPoolSize(adv_servicepool_size);
        if(server_mode == 0){
            System.out.println("begin server mode:");
            System.out.println(ms_transferlisten_port+" -> "+ms_transferhost+":"+ms_transferport);
            Server server = new Server(ms_transferhost,ms_transferport,ms_transferlisten_port,com_password);
            server.start();
        }else{
            System.out.println("begin client mode:");
            System.out.println(mc_listen+" -> "+mc_serverhost+":"+mc_serverport);
            Client client = new Client(mc_serverhost,mc_serverport,com_password,mc_listen);
            client.start();
        }
        if(adv_monitor == 1) {
            System.out.println("ThreadPoolSize:" + adv_servicepool_size);
            while (true) {
                String t = ThreadPool.currentThreadPool().status();
                System.out.print(t);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < t.length(); i++) {
                    System.out.print("\b");
                }
            }
        }
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
        //server -tl 7777 -th 192.168.0.1 -tp 3300 -p 1234567
        Option transferlisten = new Option("tl","transferlisten",true,"Set SNT-Server Transfer listen Port. ");
        Option transferhost = new Option("th","transferhost",true,"Set SNT-Server Transfer host address");
        Option transferport = new Option("tp","transferport",true,"Set SNT-Server Transfer host port");

        //client option
        //client -sh www.xxx.com -sp 7777 -p 123457 -l 11111
        Option serverhost = new Option("sh","serverhost",true,"Set the SNT-Client conect Server host address.");
        Option serverport = new Option("sp","serverport",true,"Set the SNT-Client connect Server port.");
        Option listen = new Option("l","listen",true,"Set the SNT-Client listen port.");
        //common option
        Option password = new Option("p","password",true,"Set the SNT-Client connect password.");
        //advanced option
        Option servicepoolsize = new Option("adps","servicepoolsize",true,"ThreadPool config: set servicepool size. (default :32)");
        Option display = new Option("m","monitor",false,"Display ThreadPool monitor.");

        Options options = new Options();
        options.addOption(help);
        options.addOption(server);
        options.addOption(client);
        options.addOption(transferlisten);
        options.addOption(transferhost);
        options.addOption(transferport);
        options.addOption(serverhost);
        options.addOption(serverport);
        options.addOption(password);
        options.addOption(listen);
        options.addOption(servicepoolsize);
        options.addOption(display);

        try {
            commandLine = parser.parse(options, args);
        } catch (ParseException e) {
            formatter.printHelp( formatstr, options);
            System.exit(0);
        }
        boolean printhelp = false;
        if(commandLine != null && commandLine.getOptions().length > 0){
            if(commandLine.hasOption("help")){
                printhelp = true;
            }else if(commandLine.hasOption("server")){
                server_mode = 0;
                if(commandLine.hasOption("transferlisten") && commandLine.hasOption("transferhost") && commandLine.hasOption("transferport") && commandLine.hasOption("password") ){
                    try{
                        ms_transferlisten_port = Integer.valueOf(commandLine.getOptionValue("transferlisten"));
                        ms_transferhost = commandLine.getOptionValue("transferhost");
                        ms_transferport = Integer.valueOf(commandLine.getOptionValue("transferport"));
                        com_password = commandLine.getOptionValue("password");
                    }catch (Exception e){
                        printhelp = true;
                    }
                }else{
                    printhelp = true;
                }
            }else if(commandLine.hasOption("client")){
                server_mode = 1;
                if(commandLine.hasOption("serverhost") && commandLine.hasOption("serverport") && commandLine.hasOption("password") && commandLine.hasOption("listen") ){
                    try{
                        mc_serverhost = commandLine.getOptionValue("serverhost");
                        mc_serverport = Integer.valueOf(commandLine.getOptionValue("serverport"));
                        mc_listen = Integer.valueOf(commandLine.getOptionValue("listen"));
                        com_password = commandLine.getOptionValue("password");
                    }catch (Exception e){
                        printhelp = true;
                    }
                }else{
                    printhelp = true;
                }
            }

            // advance
            if(commandLine.hasOption("servicepoolsize")){
                try{
                    adv_servicepool_size = Integer.valueOf(commandLine.getOptionValue("servicepoolsize"));
                }catch (Exception e){
                    printhelp = true;
                }
            }
            if(commandLine.hasOption("monitor")){
                adv_monitor = 1;
            }
        }else{
            printhelp = true;
        }
        if(printhelp){
            formatter.printHelp( formatstr, options);
            System.exit(0);
        }
        return commandLine;
    }
}
