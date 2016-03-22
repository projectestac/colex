    function isDecimal(num)
    //Accepts + sgn at the begining
    {
        converted = parseFloat(num*1);
        return (converted==num);
    }
    
    function isInteger(num)
    //Accepts + sgn at the begining
    {
        converted = parseInt(num*1);
        return ((-2147483648<=num) && (converted==num) && (2147483647>=num));
            
        //We need to put Java Int Limits because it seems that limits are diferent between Java and JavaScript    

    }

    function chkProperties(type,form)
    //Cal passar aquestes comprovacions al JAVA!!!!
    {
        if (type=='text')
        {
            return chkTextProperties(form.length.value);
        }
        else if (type=='decimal')
        {
            return chkDecimalProperties(form.minDec.value,form.maxDec.value,form.defaultValueDec.value); 
        }
        else if (type=='integer')
        {
            return chkIntegerProperties(form.minInt.value,form.maxInt.value,form.defaultValueInt.value); 
        }
        else if (type=='image')
        {
            return chkImageProperties(form.height.value,form.width.value);
        }
        else return true;
        
    }

    function chkTextProperties(length)
    {
        return isInteger(length) && length>0;
    }

    function chkDecimalProperties(min,max,defaultValue)
    {
        return ( isDecimal(min) && isDecimal(max) && isInteger(defaultValue) && (parseFloat(min)<=parseFloat(max)) && (parseFloat(min)<=parseFloat(defaultValue)) && (parseFloat(defaultValue)<=parseFloat(max)) );
    }

    function chkIntegerProperties(min,max,defaultValue)
    {
        return ( isInteger(min) && isInteger(max) && isInteger(defaultValue) && (parseInt(min)<=parseInt(max)) && (parseInt(min)<=parseInt(defaultValue)) && (parseInt(defaultValue)<=parseInt(max)) );
    }

    function chkImageProperties(height,width)
    //Maximum size is 300 px!!!!!!!!

    {
        return ( isInteger(height) && (height>0) && (height<=300) && isInteger(width) && (width>0) && (width<=300) )
    }

    function chkReservedChars(name,reserved)
    {
        for(i=0;i<reserved.length;i++)
        {
            chkChar = '\\'+reserved.charAt(i);

            if (name.match(chkChar)!=null) 
            {
                return false;
            }
        }
        return true;

    }

    function TrimString(sInString) 
    {
        sInString = sInString.replace( /^\s+/g, "" );// strip leading
        return sInString.replace( /\s+$/g, "" );// strip trailing
    }
