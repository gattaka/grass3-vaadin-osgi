function insert(prefix, suffix) {
	var offset = prefix.length;
	var textarea = $('.v-textarea')[0];
	if (textarea) {
		textarea.focus();

		var selection = document.selection;
		var startPos, endPos;

		// existuje výběr ?
		if (selection) {
			// MSIE
			if (navigator.userAgent.match(/msie/i)) { // ie
				// co je to range -
				// http://www.quirksmode.org/dom/range_intro.html
				// převede výběr na range
				var range = selection.createRange();

				// vytvoří kopii pro nalezení konce
				var rangeCopy = range.duplicate();

				// "Moves the current TextRange object's start and end points
				// to encase the specified HTML element object. The resulting
				// text range includes the HTML for the element, as well"
				// http://javascript.gakaa.com/textrange-movetoelementtext-4-0-5-.aspx
				rangeCopy.moveToElementText(textarea);

				// "Sets the end point of the current TextRange object to the
				// end point of another range that had previously been preserved
				// as a variable reference."
				// StartToEnd | StartToStart | EndToStart | EndToEnd
				// http://javascript.gakaa.com/textrange-setendpoint-4-0-5-.aspx
				rangeCopy.setEndPoint('EndToEnd', range);

				// získá pozici počátku ... TODO !!!
				var s = rangeCopy.text.length - range.text.length;
				startPos = s
						- (textarea.value.substr(0, s).length - textarea.value
								.substr(0, s).replace(/\r/g, '').length);

				range.text = prefix + range.text + suffix; // vepiš tam můj
				// text

			} else { // opera
				startPos = textarea.selectionStart;
			}
		} else { // gecko & webkit
			// získej místo počátku výběru
			startPos = textarea.selectionStart;

			// získej pozici posledního znaku
			endPos = textarea.selectionEnd;

			// získej obsah výběru
			selection = textarea.value.substring(startPos, endPos);

			// text před výběrem a text za ním
			var beforeInsert = textarea.value.substring(0, startPos);
			var afterInsert = textarea.value.substring(endPos,
					textarea.value.length);

			// WEBKIT (Chromium) FIX
			// ----------------------
			// Když se provede na konci textarea několik odřádkování a pak se
			// vloží tag, tak se ty prázdné řádky vloží až za ním a tag jakoby
			// "propadne nahoru" na poslední text (i mezeru)
			if (navigator.userAgent.match(/webkit/i)) {
				var c = '\n';
				var afterInsertLen = afterInsert.length;
				if (afterInsertLen > 0) {
					for (i = 0; i < afterInsertLen; i++) {
						if (afterInsert[i] != '\n') {
							c = afterInsert[i];
							break;
						}
					}
					// pokud byly všechny znaky za výběrem \n, pak se musí
					// posouvat dál (i offset se musí posunout - logicky)
					if (c == '\n') {
						// logMessage("white afterInsert shift ("+
						// afterInsertLen+")");
						beforeInsert += afterInsert;
						afterInsert = "";
						offset += afterInsertLen;
					}
				}
			}

			// nový obsah textarea je tedy beforeInsert + úprava + afterInsert
			// POZOR - tohle přenastaví pozice výběru, proto je potřeba
			// nastavení kurzoru/výběru dělat až po změně textu
			textarea.value = beforeInsert + prefix + selection + suffix
					+ afterInsert;

			// posaď kurzor tam kam se má psát
			if (startPos == endPos) {
				// pokud jde o jedno místo, posun počátek o počáteční tag
				// a konec nastav na stejné místo, co počátek
				textarea.selectionStart = startPos + offset;
				textarea.selectionEnd = textarea.selectionStart;
			} else {
				// pokud jde o výběr, posun oba dva konce o délku poč. tagu
				textarea.selectionStart = startPos + offset;
				textarea.selectionEnd = endPos + offset;
			}

			// přidej do logu události vložení tagů
			// logMessage("<strong>" + prefix +"</strong> " + "set on startPos:
			// " + startPos);
			// logMessage("<strong>" + suffix +"</strong> " + "set on endPos: "
			// + endPos);
		}
	} else {
		// logError("textarea is null");
	}
}

function registerTabListener() {
	var sr = $("vaadin-text-area")[0].shadowRoot;
	// sr.childNodes[2].childNodes[3].childNodes[3].childNodes[1].addEventListener
	var textarea = sr.children[1].children[1].children[1].children[0];
	// aby se na to opakovaně nepřidávaly další a další listenery (pak se vkládá
	// více a více tabů)
	if (textarea.tabFixedFlag == undefined) {
		textarea.addEventListener('keydown', function(e) {
			var keyCode = e.keyCode || e.which;
			if (keyCode == 9) {
				e.preventDefault();
				insert("\t", "");
			}
		}, false);
		textarea.tabFixedFlag = "fixed";
	}
}