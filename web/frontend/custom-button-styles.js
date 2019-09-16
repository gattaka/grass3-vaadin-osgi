import { html } from '@polymer/polymer/lib/utils/html-tag.js';

const $_documentContainer = html`<dom-module id="grass-lumo-button" theme-for="vaadin-button">
  <template>
    <style>
    
      :host {        
        background-color: #fcfcfc;
        border: 1px solid #ddd;
		/* aby bylo tlačítko na stejné úrovni a velikosti jako fieldy */        
        padding-bottom: 2px;    
    	height: 34px;        
      }

	  ::slotted(img) {
        margin-right: 0.50em;
        margin-bottom: -0.25em;        
      }
      
      [part="prefix"], [part="label"] {
        margin: 2px 0 0 0;
      }
      
      :host(:hover)::before {
        opacity: 0.03;
      }

    </style>
  </template>
</dom-module>`;

document.head.appendChild($_documentContainer.content);
