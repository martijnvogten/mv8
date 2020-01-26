using namespace std;
using namespace v8;

extern jclass v8ContextCls;
extern jmethodID v8CallJavaMethodID;

extern jclass v8IsolateCls;
extern jmethodID v8runIfWaitingForDebuggerMethodID;
extern jmethodID v8quitMessageLoopOnPauseMethodID;
extern jmethodID v8runMessageLoopOnPauseMethodID;
extern jmethodID v8handleInspectorMessageMethodID;

class InspectorClient;

class V8IsolateData
{
  public:
	Isolate *isolate;
	StartupData startupData;
	Persistent<ObjectTemplate> * globalObjectTemplate;
  InspectorClient * inspector;
	jobject v8;
};
