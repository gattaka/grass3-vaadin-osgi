const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `<dom-module id="grass-text-field-shared-styles" theme-for="vaadin-text-field">
  <template>
    <style>

      [part="value"],
      [part="input-field"] ::slotted(input),
      [part="input-field"] ::slotted(textarea) {
        background-color: #fff;
      }
      
      [part="input-field"] {      	
    	background-color: #fff;    	
    	border: 1px solid #ddd;    	
	  }
	  
    </style>
  </template>
</dom-module>`;

document.head.appendChild($_documentContainer.content);