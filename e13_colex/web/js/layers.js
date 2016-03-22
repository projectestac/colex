
        
        
        
        function set_layer_visibility(name, visibility)
        {
            if(document.layers)
            {
                layer = document.layers[name];
		if (layer!=null) layer.visibility=visibility;
            }
            else if (document.all)
            {
		layer = document.all[name];
		if (layer!=null) 
                {
			layer.style.visibility=visibility;
			if (layer.style.visibility=="visible") layer.style.display="inline";
		}
            }
            else if (!document.all && document.getElementById)
            {
		layer = document.getElementById(name);
		if (layer!=null) 
                {
			layer.style.visibility=visibility;
			if (layer.style.visibility=="visible") layer.style.display="inline";
            	}
            }
        }
        
        function show_layer(layerName)
        {
            set_layer_visibility(layerName, 'visible');
        }


            
        function hide_layer(layerName)
        {
            set_layer_visibility(layerName, 'hidden');
        }

        
        
