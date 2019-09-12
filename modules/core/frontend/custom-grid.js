const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `<dom-module id="my-grid" theme-for="vaadin-grid">
  <template>
    <style>
      
      [part~="cell"] ::slotted(vaadin-grid-cell-content) img {
        margin-bottom: -2px;
		width: 20px;
		color: red;
      }
      
    </style>
  </template>
</dom-module>`;

document.head.appendChild($_documentContainer.content);
