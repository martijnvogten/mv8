declare var ReactDOMServer;
declare var React;

declare function __calljava(args: string): string;

function renderHTML() {
	const html = ReactDOMServer.renderToStaticMarkup(<h1>ingrid</h1>);
	return "Henkiejaja" + html;
}
