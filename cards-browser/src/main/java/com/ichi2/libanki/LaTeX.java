/*--------------------------------------------------------------------------------------*
 * Copyright (c) 2009 Edu Zamora <edu.zasu@gmail.com>                                   *
 * Copyright (c) 2012 Kostas Spyropoulos <inigo.aldana@gmail.com>                       *
 * Copyright (c) 2015 Houssam Salem <houssam.salem.au@gmail.com>                        *
 *                                                                                      *
 * This program is free software; you can redistribute it and/or modify it under        *
 * the terms of the GNU General Public License as published by the Free Software        *
 * Foundation; either version 3 of the License, or (at your option) any later           *
 * version.                                                                             *
 *                                                                                      *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY      *
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A      *
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.             *
 *                                                                                      *
 * You should have received a copy of the GNU General Public License along with         *
 * this program.  If not, see <http://www.gnu.org/licenses/>.                           *
 *--------------------------------------------------------------------------------------*/

package com.ichi2.libanki;

import com.github.slavetto.utils.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is used to detect LaTeX tags in HTML and convert them to their corresponding image
 * file names.
 *
 * Anki provides shortcut forms for certain expressions. These three forms are considered valid
 * LaTeX tags in Anki:
 * 1 - [latex]...[/latex]
 * 2 - [$]...[$]
 * 3 - [$$]...[$$]
 *
 * Unlike the original python implementation of this class, the AnkiDroid version does not support
 * the generation of LaTeX images.
 */
public class LaTeX {

    /**
     * Patterns used to identify LaTeX tags
     */
    private static final Pattern sStandardPattern = Pattern.compile("\\[latex](.+?)\\[/latex]",
            Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    private static final Pattern sExpressionPattern = Pattern.compile("\\[\\$](.+?)\\[/\\$]",
            Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    private static final Pattern sMathPattern = Pattern.compile("\\[\\$\\$](.+?)\\[/\\$\\$]",
            Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    /**
     * Convert HTML with embedded latex tags to image links.
     * NOTE: Unlike the original python version of this method, only two parameters are required
     * in AnkiDroid. The omitted parameters are used to generate LaTeX images. AnkiDroid does not
     * support the generation of LaTeX media and the provided parameters are sufficient for all
     * other cases.
     * NOTE: _imgLink produces an alphanumeric filename so there is no need to escape the replacement string.
     */
    public static String convertLatexTagsToLinks(String html) {
        StringBuffer sb = new StringBuffer();
        Matcher matcher = sStandardPattern.matcher(html);
        while (matcher.find()) {
            matcher.appendReplacement(sb, _imgLink(matcher.group(1)));
        }
        matcher.appendTail(sb);

        matcher = sExpressionPattern.matcher(sb.toString());
        sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, _imgLink("$" + matcher.group(1) + "$"));
        }
        matcher.appendTail(sb);

        matcher = sMathPattern.matcher(sb.toString());
        sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb,
                    _imgLink("\\begin{displaymath}" + matcher.group(1) + "\\end{displaymath}"));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }


    /**
     * Return an img link for LATEX.
     */
    private static String _imgLink(String latex) {
        String txt = _latexFromHtml(latex);
        String fname = "latex-" + StringUtils.checksum(txt) + ".png";
        return "<img class=latex src=\"" + fname + "\">";
    }


    /**
     * Convert entities and fix newlines.
     */
    private static String _latexFromHtml(String latex) {
        latex = latex.replaceAll("<br( /)?>|<div>", "\n");
        latex = StringUtils.stripHTML(latex);
        return latex;
    }

}
