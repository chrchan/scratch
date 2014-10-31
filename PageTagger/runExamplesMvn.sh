#! /bin/sh

mvn -quiet exec:java -Dexec.args='http://gumgum.com' > gumgum.tagged
mvn -quiet exec:java -Dexec.args='http://www.popcrunch.com/jimmy-kimmel-engaged/' > kimmel.tagged
mvn -quiet exec:java -Dexec.args='http://gumgum-public.s3.amazonaws.com/numbers.html' > numbers.tagged
mvn -quiet exec:java -Dexec.args='http://www.windingroad.com/articles/reviews/quick-drive-2012-bmw-z4-sdrive28i/' > bmw.tagged
