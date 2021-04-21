# [AndroidViewModel vs ViewModel](https://stackoverflow.com/questions/44148966/androidviewmodel-vs-viewmodel)
## https://stackoverflow.com/a/44155403
## AndroidViewModel provides Application context
If you need to use context inside your Viewmodel you should use AndroidViewModel (AVM), because it contains the application context. To retrieve the context call getApplication(), otherwise use the regular ViewModel (VM).

AndroidViewModel has application context. We all know having static context instance is evil as it can cause memory leaks!! However, having static Application instance is not as bad as you might think because there is only one Application instance in the running application.

Therefore, using and having Application instance in a specific class is not a problem in general. But, if an Application instance references them, it is a problem because of the reference cycle problem.

## AndroidViewModel Problematic for unit tests
