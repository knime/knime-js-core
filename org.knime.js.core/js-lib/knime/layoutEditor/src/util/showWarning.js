export default () => {
	// If the function is not registered something went wrong. Show an error to the user with the link to the FAQ
	// Probably related to https://knime-com.atlassian.net/browse/AP-16763
	let containerDiv = document.createElement('div');
	containerDiv.innerHTML = 'The Layout/Configuration Editor has experienced a problem. If you are on a Linux system the following FAQ might help (https://www.knime.com/faq#q40).';
	containerDiv.classList.add('alert');
	containerDiv.classList.add('alert-warning');
	// Add ability to select text in order to copy the link
	containerDiv.style['-webkit-user-select'] = 'text';
	document.body.appendChild(containerDiv);
};
