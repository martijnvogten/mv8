var __extends = (this && this.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
        return extendStatics(d, b);
    };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
var ContentItem = /** @class */ (function (_super) {
    __extends(ContentItem, _super);
    function ContentItem() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    ContentItem.prototype.render = function () {
        var _a = this.props, title = _a.title, text = _a.text;
        return (React.createElement("div", null,
            React.createElement("h1", null, title),
            React.createElement("p", null, text)));
    };
    return ContentItem;
}(React.Component));
function renderHTML() {
    var html = ReactDOMServer.renderToStaticMarkup(React.createElement("body", null,
        React.createElement(ContentItem, { title: "Example title", text: "Example text" })));
    return html;
}
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiYnVpbGQuanMiLCJzb3VyY2VSb290IjoidHlwZXNjcmlwdC8iLCJzb3VyY2VzIjpbIm1haW4udHN4Il0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiI7Ozs7Ozs7Ozs7Ozs7QUFXQTtJQUEwQiwrQkFBNEI7SUFBdEQ7O0lBVUEsQ0FBQztJQVRBLDRCQUFNLEdBQU47UUFDTyxJQUFBLGVBQWlDLEVBQWhDLGdCQUFLLEVBQUUsY0FBeUIsQ0FBQztRQUN4QyxPQUFPLENBQ047WUFDQyxnQ0FBSyxLQUFLLENBQU07WUFDaEIsK0JBQUksSUFBSSxDQUFLLENBQ1IsQ0FDTixDQUFDO0lBQ0gsQ0FBQztJQUNGLGtCQUFDO0FBQUQsQ0FBQyxBQVZELENBQTBCLEtBQUssQ0FBQyxTQUFTLEdBVXhDO0FBRUQsU0FBUyxVQUFVO0lBQ2xCLElBQU0sSUFBSSxHQUFHLGNBQWMsQ0FBQyxvQkFBb0IsQ0FDL0M7UUFDQyxvQkFBQyxXQUFXLElBQUMsS0FBSyxFQUFDLGVBQWUsRUFBQyxJQUFJLEVBQUMsY0FBYyxHQUFFLENBQ2xELENBQ1AsQ0FBQztJQUNGLE9BQU8sSUFBSSxDQUFDO0FBQ2IsQ0FBQyJ9