#! /bin/sh

java -classpath "src/main/resources:bin:lib/stanford-postagger-3.4.1.jar:lib/commons-lang3-3.3.2.jar" com.gumgum.pageTagger.PageTagger 'http://gumgum.com' > gumgum.tagged
java -classpath "src/main/resources:bin:lib/stanford-postagger-3.4.1.jar:lib/commons-lang3-3.3.2.jar" com.gumgum.pageTagger.PageTagger 'http://www.popcrunch.com/jimmy-kimmel-engaged/' > kimmel.tagged
#java -classpath "src/main/resources:bin:lib/stanford-postagger-3.4.1.jar:lib/commons-lang3-3.3.2.jar" com.gumgum.pageTagger.PageTagger 'http://gumgum-public.s3.amazonaws.com/numbers.html' > numbers.tagged
java -classpath "src/main/resources:bin:lib/stanford-postagger-3.4.1.jar:lib/commons-lang3-3.3.2.jar" com.gumgum.pageTagger.PageTagger 'http://www.windingroad.com/articles/reviews/quick-drive-2012-bmw-z4-sdrive28i/' > bmw.tagged
