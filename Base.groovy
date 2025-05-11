import groovy.io.FileType

/**
 * Base SoapUI Objects
 * @author Darren Mallett
 * @version 1.0
 */

// ToDo previous methodName

class Base extends SoapUI
{
	//instance variables
    static String methodName
    static def pgInit = [context: SoapUI.context, log: SoapUI.log, host:'', database:'', user:'', p:''] as PostgresDb
    static def Uuid = [context: SoapUI.context, log: SoapUI.log, testRunner: SoapUI.testRunner] as Uuid
    static String file
    static def staticData = [context: SoapUI.context, log: SoapUI.log, testRunner: SoapUI.testRunner] as StaticData
    static def Dates = [context: SoapUI.context, log: SoapUI.log] as NewDates
    static def tmp

    /**
     * Method for instance Connection to Postgres database
     */
    static String connPgDb() {

        name(new Object(){}.getClass().getEnclosingMethod().getName(),pgInit,'running')
        pgInit.create('','','','')
    }

    /**
     * Method for instance to create UUIDs
     * @param cnt       The number of UUIDs to create
     */
    static String createUUID(cnt) {

        name(new Object(){}.getClass().getEnclosingMethod().getName(),cnt,'running')
        Uuid.create(cnt)
        SoapUI.uuidCount = cnt
    }

    /**
     * Method to create data variables from yaml files
     * @param file      Name of yaml file containing variables and values to be created
     */
    static String createStaticData(file) {

        name(new Object(){}.getClass().getEnclosingMethod().getName(),file,'from file')

        try {
            SoapUI.dir = new File(SoapUI.context.expand('${#Project#Data}'))
            if (!(SoapUI.dir.exists() && SoapUI.dir.isDirectory())) {
                throw new FileNotFoundException("Directory not found (" + SoapUI.dir + ")")
            }
        } finally {}

        SoapUI.dir2 = SoapUI.dir
        SoapUI.dir = SoapUI.dir.toString() + '/'

        staticData.create(SoapUI.dir,file)

        /** Create Standard Date variables */
        Dates.create()
        /** Create Environment variables */
        staticData.createEnv(SoapUI.dir, 'Env.yml')
        /** Create static data type variables */
        staticData.createEnv(SoapUI.dir, 'Static.yml')

        SoapUI.dir2.eachFileMatch(~/REQ.*.yml/) { req ->
            req = req.toString()
            req = req.replace (SoapUI.dir,"")
            staticData.createEnv(SoapUI.dir,req)
        }
    }

    /**
     * Display calling method name
     * @param obj1      Name of class
     * @param obj2      Descriptive field use as required
     * @param obj3      Descriptive field use as required
     */
    static name(obj1,obj2,obj3) {

    }
}
