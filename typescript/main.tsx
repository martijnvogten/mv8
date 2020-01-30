declare var ReactDOMServer;
declare var React;

declare function __calljava(args: string): string;

function renderHTML() {
	const html = ReactDOMServer.renderToStaticMarkup(
		<body>
			<h1>Henk</h1>
			<p>Dit is de context</p>
			<p>Zo, klik hier eens!</p>
		</body>
	);
	return html;
}
