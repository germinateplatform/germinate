/*
 *  Copyright 2018 Information and Computational Sciences,
 *  The James Hutton Institute.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

;(function ($) {
	$.fn.huttonBanner = function (options) {
		// Prepare default options
		var settings = $.extend({
			customClass: ""
		}, options);

		return this.each(function() {
			// Create the header element
			var html = '<div class="bs4-row header"><div id="hutton-banner-hunger" class="bs4-col"></div><div id="hutton-banner-education" class="bs4-col"></div><div id="hutton-banner-water" class="bs4-col"></div><div id="hutton-banner-energy" class="bs4-col"></div><div id="hutton-banner-work" class="bs4-col"></div><div class="bs4-w-100 bs4-d-md-none bs4-d-lg-none bs4-d-xl-none"></div><div id="hutton-banner-industry" class="bs4-col"></div><div id="hutton-banner-consumption" class="bs4-col"></div><div id="hutton-banner-climate" class="bs4-col"></div><div id="hutton-banner-land" class="bs4-col"></div><div id="hutton-banner-partnership" class="bs4-col"></div></div>';
			var element = $.parseHTML(html);

			// Apply optional class
			$(element).addClass(settings.customClass);

			// Append to given element
			$(this).append(element);
		});
	}
}(jQuery));