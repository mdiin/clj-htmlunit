(ns clj-htmlunit.core
  (:import (com.gargoylesoftware.htmlunit
            SgmlPage WebClient BrowserVersion)
           (com.gargoylesoftware.htmlunit.html
            HtmlElement)))

(defn make-client
  "[]
    Create a WebClient acting as Internet Explorer 7.

  [version]
   Create a WebClient acting as one version, which can
   be one of [:IE6 :IE7 :IE8 :FF3 FF3.6]."

  ([]
     (make-client :IE7))
  ([version]
     (new WebClient
          (case version
            :IE7 BrowserVersion/INTERNET_EXPLORER_7
            :IE6 BrowserVersion/INTERNET_EXPLORER_6
            :IE8 BrowserVersion/INTERNET_EXPLORER_8
            :FF3 BrowserVersion/FIREFOX_3
            :FF3.6 BrowserVersion/FIREFOX_3_6
            (throw (Exception. (str "Browser " (name version) " is unknown")))))))

(defn get-page
  "Use client to access url represented either as a
  string or an URL object. Returns a page which can be
  an HTML/XHTML/XML/SGML/Binary/Text/Unexpected page
  depending on the content type of the response."
  [client url]
  (.getPage client url))

(defprotocol XPathable
  "A protocol for pages that are queryable using XPath."

  (descendants-by-xpath [node xpath]
    "Returns all elements rooted at node matching xpath.")

  (first-descendant-by-xpath [node xpath]
    "Returns the first element rooted at node matchin
  xpath.")

  (id [node]
    "ID"))

(extend-protocol XPathable
  SgmlPage

  (descendants-by-xpath [node xpath]
    (.getByXPath node xpath))

  (first-descendant-by-xpath [node xpath]
    (.getFirstByXPath node xpath))

  (id [node] node))

(defprotocol Clickable
  "A protocol for clickable DOM elements."

  (click [node]
    "Send a click event to node. Returns a complete page."))

(extend-protocol Clickable
  HtmlElement

  (click [element]
    (.click element)))

(defn get-node-attributes
  [node]
  (let [attrs (.getAttributes node)
        length (.getLength attrs)
	items (map #(.item attrs %) (range 0 length))
	hash (reduce #(merge %1 {(keyword (.getName %2)) (.getValue %2)}) {} items)]
    hash))