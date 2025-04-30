<p data-start="288" data-end="500" class="">Thank you for considering contributing to the <strong data-start="334" data-end="359">Smart Delivery System</strong>! We welcome all kinds of contributions: new features, bug fixes, documentation improvements, UI enhancements, and performance optimizations.</p>
<hr data-start="502" data-end="505" class="">
<h2 data-start="507" data-end="530" class="">📋 Table of Contents</h2>
<ul data-start="532" data-end="928">
<li data-start="532" data-end="583" class="">
<p data-start="534" data-end="583" class=""><a data-start="534" data-end="583" class="" rel="noopener" href="#-repository-structure">📦 Repository Structure</a></p>
</li>
<li data-start="584" data-end="632" class="">
<p data-start="586" data-end="632" class=""><a data-start="586" data-end="632" class="" rel="noopener" href="#-how-to-contribute">🧑‍🏭 How to Contribute</a></p>
</li>
<li data-start="633" data-end="680" class="">
<p data-start="635" data-end="680" class=""><a data-start="635" data-end="680" class="" rel="noopener" href="#-branching-strategy">🌳 Branching Strategy</a></p>
</li>
<li data-start="681" data-end="742" class="">
<p data-start="683" data-end="742" class=""><a data-start="683" data-end="742" class="" rel="noopener" href="#-commit-message-guidelines">🎯 Commit Message Guidelines</a></p>
</li>
<li data-start="743" data-end="788" class="">
<p data-start="745" data-end="788" class=""><a data-start="745" data-end="788" class="" rel="noopener" href="#-testing-standards">🧪 Testing Standards</a></p>
</li>
<li data-start="789" data-end="842" class="">
<p data-start="791" data-end="842" class=""><a data-start="791" data-end="842" class="" rel="noopener" href="#-code-style-guidelines">📐 Code Style Guidelines</a></p>
</li>
<li data-start="843" data-end="897" class="">
<p data-start="845" data-end="897" class=""><a data-start="845" data-end="897" class="" rel="noopener" href="#-pull-request-checklist">✅ Pull Request Checklist</a></p>
</li>
<li data-start="898" data-end="928" class="">
<p data-start="900" data-end="928" class=""><a data-start="900" data-end="928" class="" rel="noopener" href="#-need-help">📞 Need Help?</a></p>
</li>
</ul>
<hr data-start="930" data-end="933" class="">
<h2 data-start="935" data-end="961" class="">📦 Repository Structure</h2>
<code class="whitespace-pre!"><span><span>├── config/                 </span><span><span class="hljs-comment"># Spring configuration classes</span></span><span>
├── controller/            </span><span><span class="hljs-comment"># REST Controllers (API layer)</span></span><span>
├── dto/                   </span><span><span class="hljs-comment"># Request and response DTOs</span></span><span>
├── enums/                 </span><span><span class="hljs-comment"># Enum definitions</span></span><span>
├── exception/             </span><span><span class="hljs-comment"># Custom and global exception handlers</span></span><span>
├── mapper/                </span><span><span class="hljs-comment"># Model-DTO mappers</span></span><span>
├── model/                 </span><span><span class="hljs-comment"># Entity classes for DB</span></span><span>
├── repository/            </span><span><span class="hljs-comment"># Spring Data JPA Repositories</span></span><span>
├── security/              </span><span><span class="hljs-comment"># Spring Security setup</span></span><span>
├── service/               </span><span><span class="hljs-comment"># Business logic services</span></span><span>
└── util/                  </span><span><span class="hljs-comment"># Utility classes</span></span><span>
</span></span></code>
<hr data-start="1561" data-end="1564" class="">
<h2 data-start="1566" data-end="1592" class="">🧑‍🏭 How to Contribute</h2>
<ol data-start="1594" data-end="1991">
<li data-start="1594" data-end="1642" class="">
<p data-start="1597" data-end="1642" class=""><strong data-start="1597" data-end="1620">Fork the repository</strong> and clone it locally.</p>
</li>
<li data-start="1643" data-end="1728" class="">
<p data-start="1646" data-end="1668" class="">Create a new branch:</p>
<code class="whitespace-pre! language-"><span><span>git checkout -b feature/my-new-feature
</span></span></code>
</li>
<li data-start="1729" data-end="1769" class="">
<p data-start="1732" data-end="1769" class="">Make your changes with clear commits.</p>
</li>
<li data-start="1770" data-end="1833" class="">
<p data-start="1773" data-end="1797" class="">Ensure all tests pass:</p>
<code class="whitespace-pre! language-bash"><span><span>./gradlew </span><span><span class="hljs-built_in">test</span></span><span>
</span></span></code>
</li>
<li data-start="1834" data-end="1941" class="">
<p data-start="1837" data-end="1881" class="">Push to your fork and create a Pull Request:</p>
<code class="whitespace-pre! language-bash"><span><span>git push origin feature/my-new-feature
</span></span></code>
</li>
<li data-start="1942" data-end="1991" class="">
<p data-start="1945" data-end="1991" class="">Wait for review and feedback from maintainers.</p>
</li>
</ol>
<hr data-start="1993" data-end="1996" class="">
<h2 data-start="1998" data-end="2022" class="">🌳 Branching Strategy</h2>


Branch Name | Purpose|
-- | --
main | Latest stable release|
dev | Active development branch|
feature/* | New features|
bugfix/* | Bug fixes|
hotfix/* | Emergency production fixes|
docs/* | Documentation changes|
test/* | Test coverage enhancements|


<p data-start="2556" data-end="2565" class="">Examples:</p>
<ul data-start="2566" data-end="2648">
<li data-start="2566" data-end="2596" class="">
<p data-start="2568" data-end="2596" class=""><code data-start="2568" data-end="2596">feature/parcel-tracking-ui</code></p>
</li>
<li data-start="2597" data-end="2625" class="">
<p data-start="2599" data-end="2625" class=""><code data-start="2599" data-end="2625">bugfix/fix-payment-error</code></p>
</li>
<li data-start="2626" data-end="2648" class="">
<p data-start="2628" data-end="2648" class=""><code data-start="2628" data-end="2648">docs/update-readme</code></p>
</li>
</ul>
<hr data-start="2650" data-end="2653" class="">
<h2 data-start="2655" data-end="2686" class="">🎯 Commit Message Guidelines</h2>
<p data-start="2688" data-end="2766" class="">Follow <a data-start="2695" data-end="2765" rel="noopener" target="_new" class="cursor-pointer">Conventional Commits</a>:</p>
<code class="whitespace-pre!"><span><span><span class="language-xml"><span class="hljs-tag">&lt;<span class="hljs-name">type</span></span></span></span><span>&gt;(scope): </span><span><span class="hljs-tag">&lt;<span class="hljs-name">short</span></span></span><span> </span><span><span class="hljs-attr">summary</span></span><span>&gt;
</span></span></code>

---

<p data-start="2808" data-end="2878" class=""><strong data-start="2808" data-end="2817">Types</strong>: <code data-start="2819" data-end="2825">feat</code>, <code data-start="2827" data-end="2832">fix</code>, <code data-start="2834" data-end="2840">docs</code>, <code data-start="2842" data-end="2849">style</code>, <code data-start="2851" data-end="2861">refactor</code>, <code data-start="2863" data-end="2869">test</code>, <code data-start="2871" data-end="2878">chore</code></p>
<p data-start="2880" data-end="2893" class=""><strong data-start="2880" data-end="2892">Examples</strong>:</p>
<ul data-start="2894" data-end="3024">
<li data-start="2894" data-end="2941" class="">
<p data-start="2896" data-end="2941" class=""><code data-start="2896" data-end="2941">feat(parcel): add live tracking for parcels</code></p>
</li>
<li data-start="2942" data-end="2983" class="">
<p data-start="2944" data-end="2983" class=""><code data-start="2944" data-end="2983">fix(auth): resolve JWT expiration bug</code></p>
</li>
<li data-start="2984" data-end="3024" class="">
<p data-start="2986" data-end="3024" class=""><code data-start="2986" data-end="3024">docs(readme): add setup instructions</code></p>
</li>
</ul>
<hr data-start="3026" data-end="3029" class="">
<h2 data-start="3031" data-end="3054" class="">🧪 Testing Standards</h2>
<ul data-start="3056" data-end="3216">
<li data-start="3056" data-end="3119" class="">
<p data-start="3058" data-end="3119" class="">All new features <strong data-start="3075" data-end="3118">must include unit and integration tests</strong>.</p>
</li>
<li data-start="3120" data-end="3172" class="">
<p data-start="3122" data-end="3172" class="">Use <strong data-start="3126" data-end="3135">JUnit</strong> and <strong data-start="3140" data-end="3151">Mockito</strong> for backend testing.</p>
</li>
<li data-start="3173" data-end="3216" class="">
<p data-start="3175" data-end="3216" class="">Validate with <code data-start="3189" data-end="3205">./gradlew test</code> before PR.</p>
</li>
</ul>
<hr data-start="3218" data-end="3221" class="">
<h2 data-start="3223" data-end="3250" class="">📐 Code Style Guidelines</h2>
<ul data-start="3252" data-end="3433">
<li data-start="3252" data-end="3262" class="">
<p data-start="3254" data-end="3262" class="">Java 17+</p>
</li>
<li data-start="3263" data-end="3349" class="">
<p data-start="3265" data-end="3349" class="">Follow <a data-start="3272" data-end="3349" rel="noopener" target="_new" class="cursor-pointer">Google Java Style Guide</a></p>
</li>
<li data-start="3350" data-end="3433" class="">
<p data-start="3352" data-end="3433" class="">Format code using built-in IntelliJ formatter or <code data-start="3401" data-end="3411">spotless</code> plugin (if included).</p>
</li>
</ul>
<hr data-start="3435" data-end="3438" class="">
<h2 data-start="3440" data-end="3467" class="">✅ Pull Request Checklist</h2>
<p data-start="3469" data-end="3492" class="">Before you submit a PR:</p>
<ul data-start="3494" data-end="3779" class="contains-task-list">
<li data-start="3494" data-end="3534" class="">
<p data-start="3500" data-end="3534" class=""><input disabled="" type="checkbox"> Code compiles and passes all tests</p>
</li>
<li data-start="3535" data-end="3579" class="">
<p data-start="3541" data-end="3579" class=""><input disabled="" type="checkbox"> Linting passes (no major style issues)</p>
</li>
<li data-start="3580" data-end="3629" class="">
<p data-start="3586" data-end="3629" class=""><input disabled="" type="checkbox"> PR linked to a GitHub Issue (if applicable)</p>
</li>
<li data-start="3630" data-end="3675" class="">
<p data-start="3636" data-end="3675" class=""><input disabled="" type="checkbox"> PR follows the branch naming convention</p>
</li>
<li data-start="3676" data-end="3734" class="">
<p data-start="3682" data-end="3734" class=""><input disabled="" type="checkbox"> All dependencies updated in <code data-start="3710" data-end="3724">build.gradle</code> if needed</p>
</li>
<li data-start="3735" data-end="3779" class="">
<p data-start="3741" data-end="3779" class=""><input disabled="" type="checkbox"> Clear title and meaningful description</p>
</li>
</ul>
<hr data-start="3781" data-end="3784" class="">
<h2 data-start="3786" data-end="3802" class="">📞 Need Help?</h2>
<p data-start="3804" data-end="3868" class="">If you're stuck or unsure about how to contribute, feel free to:</p>
<ul data-start="3870" data-end="3999">
<li data-start="3870" data-end="3932" class="">
<p data-start="3872" data-end="3932" class="">Create a <a data-start="3881" data-end="3932" rel="noopener" target="_new" class="" href="https://github.com/Learnathon-By-Geeky-Solutions/byte-breeze/issues">GitHub Issue</a></p>
</li>
<li data-start="3933" data-end="3960" class="">
<p data-start="3935" data-end="3960" class="">Reach out to a maintainer</p>
</li>
<li data-start="3961" data-end="3999" class="">
<p data-start="3963" data-end="3999" class="">Tag your question with <code data-start="3986" data-end="3999">help wanted</code></p></li></ul><!--EndFragment-->
