/**
 * Assertion calls
 * @author Darren Mallett
 * @version 1.0
 */

// ToDo package
// ToDo get import com.eviware.soapui.support.GroovyUtils
// ToDo parameter for log.info

class Assertion extends SoapUI {

    /**
     * Contains method for assertion
     * @param field     name of the Column / Field from the Row / Document, being checked in the assertion
     * @param value     value associated with the Column / Field
     */
    String contains(field,value) {

        /** Expected value */
        SoapUI.exp_value = ''

        SoapUI.exp_value = field + ':'

        SoapUI.exp_value = SoapUI.exp_value + SoapUI.context.expand(value)

        def a = contain(SoapUI.exp_value)
		return a
	}

    /**
     * Assertion method for array lists
     * @param p     key value map
     */
    String assertion(Map p=[:]) {

		def cnt = 0, cnt1 = 0, cnt2 = 0

		p.each {cnt += 1}		//  this should equal 2 (keys and assertions)

		p.par.each {cnt1 += 1}	//  keys

		p.chk.each {cnt2 += 1}	//  assertions

		def r = mapcnt(cnt,cnt1,cnt2)
		
		if (r.startsWith('SUCCESSFUL')) {

            SoapUI.exp_value = ''

            SoapUI.exp_value = p.par[0] + ':'

            if (p.chk[0].startsWith('$')) {	//  Custom Property

                SoapUI.exp_value = SoapUI.exp_value + SoapUI.context.expand(p.chk[0])

            } else {

                SoapUI.exp_value = SoapUI.exp_value + p.chk[0] + '"'
            }

            def a = contain(SoapUI.exp_value)
            return a

		} else {
		
			return "Array Elements: " + cnt + "  Parameter Elements: " + cnt1 + "  Check Elements: " + cnt2 + "(" + a + ")"		
		}
  	}

    /**
     * Method used to validate array lists before assertion
     * @param cnt       count of array lists
     * @param cnt1      count of keys
     * @param cnt2      count of assertions
     */
  	def static mapcnt(cnt,cnt1,cnt2) {

  		if (cnt != 2) {
  			return "ERROR: Expected 2 sets of [] brackets (" + cnt + " sent)"  		
		} else if (cnt1 != cnt2) {
			return "ERROR: Expected number of fields (" + cnt1 + ") to match number of checks (" + cnt2 + ")"
		} else if (cnt1 == 0 && cnt2 == 0) {
			return "ERROR: Parameters not defined"
		} else {
  			return "SUCCESSFUL: " + cnt1 + " " + cnt2
		}
  	}

    /**
     * Method not implemented (to be used with lists and transformations)
     * @param p         key value map
     * @param cnt1      number of keys
     * @param resp      response
     */
  	def static assertexec(p,cnt1,resp) {  // version 2 context & log parameter 

  		def arr = ''
        SoapUI.exp_value = ''
        SoapUI.act_value = ''

  		for(i in 0 .. cnt1-1) {

			SoapUI.exp_value = p.chk[i]

            if (resp.indexOf(p.par[i]) > -1) {
                SoapUI.act_value = p.par[i]
            } else {
                SoapUI.act_value = "Not Found: " + p.par[i] + " in " + resp
            }

			if (SoapUI.exp_value.startsWith('$')) {	//  Custom Property

                SoapUI.exp_value = SoapUI.context.expand(SoapUI.exp_value)

				//exp_value.replace(",",'","')
						
				if (SoapUI.exp_value.contains('.')) {
	
					def pos = SoapUI.exp_value.indexOf('.')

                    SoapUI.exp_value = cusPropRepAll(SoapUI.exp_value)	//  ReplaceAll string

                    SoapUI.exp_value = cusPropUpCase(SoapUI.exp_value)		//  UpperCase string
				
					pos = SoapUI.exp_value.indexOf('.')
                    SoapUI.exp_value = SoapUI.exp_value.substring(0,pos)		//	Remove string parameters

				}
					
   				//log.info "Expected: " + exp_value + "  Actual: " + act_value
				assert SoapUI.exp_value == SoapUI.act_value

   			} else {	//  Non-equal assertions						

				//log.info "Expected: " + act_value + " " + exp_value

				switch (SoapUI.exp_value.substring(0,2)) {

					case '<>':
                        SoapUI.exp_value = propNotEqual(SoapUI.exp_value, SoapUI.act_value)
						break
			
					default:
                        try
                        {
                            SoapUI.log.info "Unknown Expected Value: " + SoapUI.exp_value.substring(0,3)
                            throw new IllegalArgumentException("Expected: '<>'  Actual: " + SoapUI.exp_value.substring(0,3))
                        } finally {break}
				}
			}
  		}
  	}

    /**
     * Contain method for checking that Actual value contains the Expected value
     * @param exp_value     Expected value
     */
    def static contain(exp_value) {

        SoapUI.act_value = SoapUI.response

   		//log.info "Expected: " + exp_value + "  within  " + act_value

		assert SoapUI.act_value.contains(exp_value)
  	}

    /**
     * Method removes all specified characters from Expected Value where the Actual value is transformed
     * @param exp_value     Expected value
     */
  	def static cusPropRepAll(exp_value) {
				
		if (exp_value.toLowerCase().contains('replaceall')) {
			int i1 = exp_value.toLowerCase().indexOf('replaceall')
			int i2 = exp_value.toLowerCase().indexOf(']')
			int i3 = i1+13
			def chr = exp_value[i3..i2-1]
			//log.info "ReplaceAll " + "[" + chr + "]"
			chr.each {
				exp_value = exp_value.replaceAll(it,'')
			}
		}
		return exp_value
  	}

    /**
     * Method upcases Expected value (where the Actual value is transformed)
     * @param exp_value     Expected value
     */
  	def static cusPropUpCase(exp_value) {

		if (exp_value.toLowerCase().contains('uppercase')) {
		
			exp_value = exp_value.toUpperCase()
		}

		return exp_value
  	}

    /**
     * Method used for Not-Equal to assertion
     * @param exp_value     Expected value
     * @param act_value     Actual value
     */
  	def static propNotEqual(exp_value,act_value) {
  
		assert act_value != exp_value.replaceAll('[<>]','')
		return exp_value
  	}

    /**
     * Use this method to log Assertion call
     */
  	def static report() { SoapUI.log.info "Assert call" }

}
