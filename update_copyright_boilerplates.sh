#!/bin/sh

# This script is licensed under Creative Commons Zero (CC0 / Public Domain).

ROOT="$(dirname $0)"
CUR_YEAR=$(date +"%Y")
HOLDERS="Arianne Stendhal Marauroa Faiumoni_e.V. Faiumoni_e._V."
PROJECT="Arianne"
DIRS="bot client common server tools"
#DIRS="client"


YEAR_ONLY=1

if [ "$YEAR_ONLY" -gt "0" ]; then
	echo "Updating boilerplate year(s) in source files ...";

	for DIR in $DIRS; do
		echo "Updating $DIR files ...";

		SOURCES=$(find "$ROOT/src/games/stendhal/$DIR/" -type f -name "*.java");
		for JAVA in $SOURCES; do
			YEAR=$(grep "(C) Copyright" "$JAVA" | sed -e 's|\(.*\)Copyright ||' -e 's|\([0-4]*\)\(.*\)|\1|');
			if [ "$YEAR" == "$CUR_YEAR" ]; then
				YEARS="$CUR_YEAR";
			else
				YEARS="$YEAR-$CUR_YEAR";
			fi;

			# Update year(s) for each Java source
			sed -i -e "s|Copyright $YEAR$\(.*\) \([.*]*\)\*|Copyright $YEARS \1\*|" "$JAVA";

			RET_VALUE=$?
			if [ "$RET_VALUE" -gt "0" ]; then
				echo "An error occurred (error code: $RET_VALUE). Exiting ...";
				exit $RET_VALUE;
			fi
		done;
	done;
else
	echo "Updating boilerplate year(s) & copyright holder in source files ...";

	for DIR in $DIRS; do
		echo "Updating $DIR files ...";

		SOURCES=$(find "$ROOT/src/games/stendhal/$DIR/" -type f -name "*.java");
		for JAVA in $SOURCES; do
			YEAR=$(grep "(C) Copyright" "$JAVA" | sed -e 's|\(.*\)Copyright ||' -e 's|\([0-4]*\)\(.*\)|\1|');
			for HOLDER in $HOLDERS; do
				HOLDER="$(echo "$HOLDER" | sed -e 's|_| |g')";
				if [ "$YEAR" == "$CUR_YEAR" ]; then
					YEARS="$CUR_YEAR";
				else
					YEARS="$YEAR-$CUR_YEAR";
				fi;

				# Update year(s) & copyright holder for each Java source
				sed -i -e "s|Copyright $YEAR\(.* \)$HOLDER\(.*\)\*|Copyright $YEARS - $PROJECT\t\t\t\t\t   \*|" "$JAVA";

				RET_VALUE=$?
				if [ "$RET_VALUE" -gt "0" ]; then
					echo "An error occurred (error code: $RET_VALUE). Exiting ...";
					exit $RET_VALUE;
				fi
			done;
		done;
	done
fi

echo "Done."
