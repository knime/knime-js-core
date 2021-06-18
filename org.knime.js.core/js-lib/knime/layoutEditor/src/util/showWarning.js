export default () => {
	// This methods displays a warning to the user that the layout/configuration editor does not properly work.
	// Probably related to https://knime-com.atlassian.net/browse/AP-16763
	let containerDiv = document.createElement('div');
	containerDiv.innerHTML = 'The Layout/Configuration Editor has experienced a problem. The following FAQ might help (https://www.knime.com/faq#q7). You can still use the advanced tab to set the layout.';
	containerDiv.classList.add('alert');
	containerDiv.classList.add('alert-warning');
	// Add ability to select text in order to copy the link
	containerDiv.style['-webkit-user-select'] = 'text';
	document.body.appendChild(containerDiv);
};
