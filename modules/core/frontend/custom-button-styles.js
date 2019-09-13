import { html } from '@polymer/polymer/lib/utils/html-tag.js';

const $_documentContainer = html`<dom-module id="grass-lumo-button" theme-for="vaadin-button">
  <template>
    <style>
    
      :host {        
        background-color: #fcfcfc;
        border: 1px solid #ddd;
      }

	  [part="prefix"] {
        margin-bottom: -0.25em;
        margin-left: 0em;
        margin-right: 0.50em;        
      }
      
      :host(:hover)::before {
        opacity: 0.03;
      }

    </style>
  </template>
</dom-module>`;

document.head.appendChild($_documentContainer.content);
